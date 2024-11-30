package ferrum

import arc.func.Prov
import arc.graphics.Color
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Scaling
import arc.util.Strings
import mindustry.content.*
import mindustry.content.TechTree.TechNode
import mindustry.game.Objectives.SectorComplete
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.ui.Fonts
import mindustry.ui.Styles
import mindustry.world.Tile
import mindustry.world.blocks.production.Drill
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import mindustry.world.meta.StatValues
import kotlin.math.max
import kotlin.random.Random

fun Ferrum.addDrills() {
    pyriteExtractor = object : Drill("pyrite-extractor") {
        init {
            researchCost = ItemStack.with(Items.copper, 1000, Items.lead, 500, Items.graphite, 200)
            alwaysUnlocked = false
            techNode = TechNode(Blocks.mechanicalDrill.techNode, this, researchCost).also {
                it.objectives = Seq.with(SectorComplete(SectorPresets.frozenForest))
            }
        }

        override fun canMine(tile: Tile?): Boolean {
            return tile?.drop() == Items.coal
        }

        override fun countOre(tile: Tile?) {
            super.countOre(tile)
            returnItem = pyrite
        }

        override fun setStats() {
            super.setStats()

            stats.remove(Stat.drillTier)
            stats.add(Stat.drillTier) { table: Table ->
                val blockData = listOf(
                    Triple(Blocks.oreCoal, Items.coal, 2f),
                    Triple(Blocks.oreCoal, pyrite, 1f)
                )
                val drillMultiplier = hardnessDrillMultiplier
                val multipliers = drillMultipliers
                table.row()
                table.table { c: Table ->
                    var i = 0
                    for (data in blockData) {
                        val block = data.first
                        val itemDrop = data.second
                        val mult = data.third
                        c.table(Styles.grayPanel) { b: Table ->
                            b.image(block.uiIcon).size(40f).pad(10f).left().scaling(Scaling.fit)
                            b.table { info: Table ->
                                info.left()
                                info.add(block.localizedName).left().row()
                                info.run {
                                    if (itemDrop.hasEmoji())
                                        return@run add(itemDrop.emoji())
                                    else
                                        return@run image(itemDrop.uiIcon).size((Fonts.def.data.lineHeight / Fonts.def.data.scaleY))
                                }.left()
                            }.grow()
                            if (multipliers != null) {
                                b.add(
                                    Strings.autoFixed(
                                        (60f * mult / (max(
                                            (drillTime + drillMultiplier * block.itemDrop.hardness).toDouble(),
                                            drillTime.toDouble()
                                        ) / multipliers.get(block.itemDrop, 1f)) * size).toFloat(), 2
                                    ) + StatUnit.perSecond.localized()
                                )
                                    .right().pad(10f).padRight(15f).color(Color.lightGray)
                            }
                        }.growX().pad(5f)
                        if (++i % 2 == 0) c.row()
                    }
                }.growX().colspan(table.columns)
            }
        }
    }.apply {
        buildType = Prov {
            object : Drill.DrillBuild() {
                override fun offload(item: Item?) {
                    if (Random.nextFloat() < 2/3f)
                        super.offload(Items.coal)
                    else
                        super.offload(item)
                }
            }
        }
    }.apply {
        requirements(
            Category.production, ItemStack.with(Items.lead, 100, Items.graphite, 30)
        )
        drillTime = (Blocks.laserDrill as Drill).drillTime
        size = 3
        hasPower = true
        tier = 4
        updateEffect = Fx.pulverizeMedium
        drillEffect = Fx.mineBig

        consumePower(0.5f)
        consumeLiquid(Liquids.water, 0.1f).boost()
    }

    ironExtractor = object : Drill("iron-extractor") {
        init {
            researchCost = ItemStack.with(Items.copper, 2000, iron, 500, Items.silicon, 500)
            alwaysUnlocked = false
            techNode = TechNode(pyriteExtractor.techNode, this, researchCost)
        }

        override fun canMine(tile: Tile?): Boolean {
            return tile?.drop() == Items.titanium
        }

        override fun countOre(tile: Tile?) {
            super.countOre(tile)
            returnItem = iron
        }

        override fun setStats() {
            super.setStats()

            stats.remove(Stat.drillTier)
            stats.add(
                Stat.drillTier, StatValues.drillables(
                    drillTime, hardnessDrillMultiplier, (size * size).toFloat(), drillMultipliers
                ) { it.itemDrop == Items.titanium })
        }
    }.apply {
        requirements(
            Category.production, ItemStack.with(Items.copper, 80, Items.silicon, 30, iron, 20)
        )
        drillTime = 280f
        size = 3
        hasPower = true
        tier = 4
        updateEffect = Fx.pulverizeMedium
        drillEffect = Fx.mineBig

        consumePower(2f)
        consumeLiquid(Liquids.cryofluid, 0.1f).boost()
    }
}