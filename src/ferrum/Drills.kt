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
import mindustry.world.blocks.environment.Floor
import mindustry.world.blocks.production.Drill
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import mindustry.world.meta.StatValue
import kotlin.math.max
import kotlin.math.sqrt
import kotlin.random.Random

private data class Byproduct(val item: Item, val chance: Float) {
    fun roll(): Item? {
        return takeIf { Random.nextFloat() < chance }?.item
    }

    fun roll(updateChance: (Float) -> Float): Item? {
        return takeIf { Random.nextFloat() < updateChance(chance) }?.item
    }
}

private val Ferrum.byproducts
    get() = mapOf(
        Items.coal to Byproduct(pyrite, 1 / 3f), Items.titanium to Byproduct(iron, 1 / 2f)
    )

fun Ferrum.loadDrills() {
    fun Drill.getCustomDrillTierStat(): StatValue {
        return StatValue { table: Table ->
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
                                byproducts[block.itemDrop]?.item?.let {
                                    if (it.hasEmoji()) itemTable.add(it.emoji())
                                    else itemTable.image(it.uiIcon)
                                        .size((Fonts.def.data.lineHeight / Fonts.def.data.scaleY))
                                }?.left()
                            }.left()

                        }.grow()
                        if (multipliers != null) {
                            val area = size * size
                            b.add(
                                Strings.autoFixed(
                                    (60f / (max(
                                        (drillTime + drillMultiplier * block.itemDrop.hardness).toDouble(),
                                        drillTime.toDouble()
                                    ) / multipliers.get(block.itemDrop, 1f)) * area).toFloat(), 2
                                ) + StatUnit.perSecond.localized()
                            ).right().pad(10f).padRight(15f).color(Color.lightGray)
                        }
                    }.growX().pad(5f)
                    if (++i % 2 == 0) c.row()
                }
            }.growX().colspan(table.columns)
        }
    }

    smartDrill = object : Drill("smart-drill") {
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
                        val byproduct: Item? = byproducts[item]?.roll()
                        super.offload(byproduct ?: item)
                    }
                }
            }
        }

        private fun setCustomDrillTierStat() {
            stats.remove(Stat.drillTier)
            stats.add(Stat.drillTier, getCustomDrillTierStat())
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

    traceDrill = object : Drill("trace-drill") {
        init {
            buildType = Prov {
                object : DrillBuild() {
                    override fun offload(item: Item?) {
                        val byproduct: Item? = byproducts[item]?.roll {
                            // increased chance
                            sqrt(it)
                        }
                        super.offload(byproduct ?: item)
                    }
                }
            }
        }

        override fun setStats() {
            super.setStats()

            stats.remove(Stat.drillTier)
            stats.add(Stat.drillTier, getCustomDrillTierStat())
        }
    }.apply {
        requirements(
            Category.production, ItemStack.with(Items.silicon, 80, Items.titanium, 50, Items.thorium, 50, steel, 50)
        )
        consumePower(4.5f)
        consumeLiquid(Liquids.cryofluid, 0.1f).boost()
        drillTime = (Blocks.blastDrill as Drill).drillTime * 1.2f
        heatColor = Color.cyan.saturation(0.25f)
        warmupSpeed = 0.005f

        // copied from vanilla blast drill
        size = 4
        drawRim = true
        hasPower = true
        tier = 5
        updateEffect = Fx.pulverizeRed
        updateEffectChance = 0.03f
        drillEffect = Fx.mineHuge
        rotateSpeed = 6f
        itemCapacity = 20
        liquidBoostIntensity = 2.0f
    }
}

fun Ferrum.byproductifyVanillaDrills() {
    fun Drill.addByproducts() {
        buildType = Prov {
            object : Drill.DrillBuild() {
                override fun offload(item: Item?) {
                    super.offload(byproducts[item]?.roll {
                        // lowered chance
                        it * it
                    } ?: item)
                }
            }
        }
    }

    Vars.content.blocks().filterIsInstance<Drill>().filter { it.isVanilla }.forEach { it.apply(Drill::addByproducts) }
}