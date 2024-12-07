package ferrum

import arc.func.Boolf
import arc.func.Prov
import arc.graphics.Color
import arc.scene.ui.layout.Table
import arc.util.Scaling
import arc.util.Strings
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.ui.Fonts
import mindustry.ui.Styles
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.blocks.environment.Floor
import mindustry.world.blocks.production.Drill
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import mindustry.world.meta.StatValue
import kotlin.math.max
import kotlin.random.Random

fun Ferrum.loadDrills() {
    smartDrill = object : Drill("smart-drill") {
        val byproducts = mapOf(Items.coal to pyrite, Items.titanium to iron)
        val byproductChance = 1 / 3f

        private fun getLiquidBoostIntensity(liquid: Liquid): Float =
            1 + 0.6f * liquid.heatCapacity / Liquids.water.heatCapacity

        init {
            buildType = Prov {
                object : DrillBuild() {
                    override fun updateTile() {
                        liquidBoostIntensity = getLiquidBoostIntensity(liquids.current())
                        super.updateTile()
                        liquidBoostIntensity = getLiquidBoostIntensity(Liquids.water)
                    }

                    override fun offload(item: Item?) {
                        // item is the byproduct if byproductable
                        if (byproducts.containsValue(item) && byproductChance < Random.nextFloat()) super.offload(
                            byproducts.entries.first { it.value == item }.key
                        )
                        else super.offload(item)
                    }
                }
            }
        }

        override fun countOre(tile: Tile?) {
            super.countOre(tile)
            returnItem = byproducts[tile?.drop()] ?: return
        }

        private fun setCustomDrillTierStat() {
            stats.remove(Stat.drillTier)

            val statValue = StatValue { table: Table ->
                val drillMultiplier = hardnessDrillMultiplier
                val filter = Boolf { b: Block ->
                    b is Floor && !b.wallOre && b.itemDrop != null && b.itemDrop.hardness <= tier && b.itemDrop !== blockedItem && (Vars.indexer.isBlockPresent(
                        b
                    ) || Vars.state.isMenu)
                }
                val multipliers = drillMultipliers

                table.row()
                table.table { c: Table ->
                    var i = 0
                    for (block in Vars.content.blocks()) {
                        if (!filter.get(block)) continue

                        c.table(Styles.grayPanel) { b: Table ->
                            b.image(block.uiIcon).size(40f).pad(10f).left().scaling(Scaling.fit)
                            b.table { info: Table ->
                                info.left()
                                info.add(buildString {
                                    append(block.localizedName)
                                    append(byproducts[block.itemDrop]?.let { "+" } ?: return@buildString)
                                }).left().row()
                                info.table { itemTable ->
                                    itemTable.add(block.itemDrop.emoji())
                                    byproducts[block.itemDrop]?.let {
                                        if (it.hasEmoji()) itemTable.add(it.emoji())
                                        else itemTable.image(it.uiIcon)
                                            .size((Fonts.def.data.lineHeight / Fonts.def.data.scaleY))
                                    }?.left()
                                }.left()

                            }.grow()
                            if (multipliers != null) {
                                b.add(
                                    Strings.autoFixed(
                                        (60f / (max(
                                            (drillTime + drillMultiplier * block.itemDrop.hardness).toDouble(),
                                            drillTime.toDouble()
                                        ) / multipliers.get(block.itemDrop, 1f)) * size).toFloat(), 2
                                    ) + StatUnit.perSecond.localized()
                                ).right().pad(10f).padRight(15f).color(Color.lightGray)
                            }
                        }.growX().pad(5f)
                        if (++i % 2 == 0) c.row()
                    }
                }.growX().colspan(table.columns)
            }
            stats.add(Stat.drillTier, statValue)
        }

        private fun getRealLiquidBoostMultiplier(liquid: Liquid): Float =
            getLiquidBoostIntensity(liquid).let { it * it }

        private fun setCustomBoosterStat() {
            stats.remove(Stat.booster)

            val statValue = StatValue { table: Table ->
                val unit = "{0}" + StatUnit.timesSpeed.localized()
                val amount = 0.06f
                val filter = { liq: Liquid? -> this.consumesLiquid(liq) }

                table.row()
                table.table { c: Table ->
                    for (liquid in Vars.content.liquids()) {
                        if (!filter(liquid)) continue

                        c.table(Styles.grayPanel) { b: Table ->
                            b.image(liquid.uiIcon).size(40f).pad(10f).left().scaling(Scaling.fit)
                            b.table { info: Table ->
                                info.add(liquid.localizedName).left().row()
                                info.add(Strings.autoFixed(amount * 60f, 2) + StatUnit.perSecond.localized()).left()
                                    .color(Color.lightGray)
                            }
                            b.table { bt: Table ->
                                bt.right().defaults().padRight(3f).left()
                                bt.add(
                                    unit.replace(
                                        "{0}", "[stat]" + Strings.autoFixed(
                                            getRealLiquidBoostMultiplier(liquid), 2
                                        ) + "[lightgray]"
                                    )
                                ).pad(5f)
                            }.right().grow().pad(10f).padRight(15f)
                        }.growX().pad(5f).row()
                    }
                }.growX().colspan(table.columns)
                table.row()
            }

            stats.add(Stat.booster, statValue)
        }

        override fun setStats() {
            super.setStats()
            setCustomDrillTierStat()
            setCustomBoosterStat()
        }
    }.apply {
        requirements(Category.production, ItemStack.with(Items.copper, 18, Items.silicon, 10))
        consumePower(0.1f)
        consumeCoolant(0.06f).boost()

        // copied from pneumatic drill
        tier = 3
        drillTime = 400f
        size = 2
    }

    pyriteExtractor = object : Drill("pyrite-extractor") {
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
                    Triple(Blocks.oreCoal, Items.coal, 2f), Triple(Blocks.oreCoal, pyrite, 1f)
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
                                info.add(itemDrop.localizedName).left().row()
                                info.run {
                                    if (itemDrop.hasEmoji()) return@run add(itemDrop.emoji())
                                    else return@run image(itemDrop.uiIcon).size((Fonts.def.data.lineHeight / Fonts.def.data.scaleY))
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
                                ).right().pad(10f).padRight(15f).color(Color.lightGray)
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
                    if (Random.nextFloat() < 2 / 3f) super.offload(Items.coal)
                    else super.offload(item)
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
            stats.add(Stat.drillTier) { table: Table ->
                val blockData = listOf(
                    Blocks.oreTitanium to Items.titanium, Blocks.oreTitanium to iron
                )
                val drillMultiplier = hardnessDrillMultiplier
                val multipliers = drillMultipliers
                table.row()
                table.table { c: Table ->
                    var i = 0
                    for (data in blockData) {
                        val block = data.first
                        val itemDrop = data.second
                        c.table(Styles.grayPanel) { b: Table ->
                            b.image(block.uiIcon).size(40f).pad(10f).left().scaling(Scaling.fit)
                            b.table { info: Table ->
                                info.left()
                                info.add(itemDrop.localizedName).left().row()
                                info.run {
                                    if (itemDrop.hasEmoji()) return@run add(itemDrop.emoji())
                                    else return@run image(itemDrop.uiIcon).size((Fonts.def.data.lineHeight / Fonts.def.data.scaleY))
                                }.left()
                            }.grow()
                            if (multipliers != null) {
                                b.add(
                                    Strings.autoFixed(
                                        (60f / (max(
                                            (drillTime + drillMultiplier * block.itemDrop.hardness).toDouble(),
                                            drillTime.toDouble()
                                        ) / multipliers.get(block.itemDrop, 1f)) * size).toFloat(), 2
                                    ) + StatUnit.perSecond.localized()
                                ).right().pad(10f).padRight(15f).color(Color.lightGray)
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
                    if (Random.nextFloat() < 0.5f) super.offload(Items.titanium)
                    else super.offload(item)
                }
            }
        }
    }.apply {
        requirements(
            Category.production, ItemStack.with(Items.copper, 100, Items.silicon, 50, iron, 20)
        )
        drillTime = (Blocks.laserDrill as Drill).drillTime * 0.6f
        size = 3
        hasPower = true
        tier = 4
        updateEffect = Fx.pulverizeMedium
        drillEffect = Fx.mineBig

        consumePower(2f)
        consumeLiquid(Liquids.cryofluid, 0.1f).boost()
    }
}