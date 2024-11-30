package ferrum

import arc.func.Prov
import arc.struct.Seq
import mindustry.content.*
import mindustry.content.TechTree.TechNode
import mindustry.game.Objectives.SectorComplete
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.Tile
import mindustry.world.blocks.production.Drill
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValues

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
            stats.add(
                Stat.drillTier, StatValues.drillables(
                drillTime, hardnessDrillMultiplier,
                (size * size).toFloat(), drillMultipliers
            ) { it.itemDrop == Items.coal })
        }
    }.apply {
        buildType = Prov { object : Drill.DrillBuild() {
            override fun offload(item: Item?) {
                super.offload(Items.coal)
                super.offload(Items.coal)
                super.offload(item)
            }
        } }
    }.apply {
        requirements(
            Category.production,
            ItemStack.with(Items.lead, 100, Items.graphite, 30)
        )
        drillTime = (Blocks.laserDrill as Drill).drillTime * 3f
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
                drillTime, hardnessDrillMultiplier,
                (size * size).toFloat(), drillMultipliers
            ) { it.itemDrop == Items.titanium })
        }
    }.apply {
        requirements(
            Category.production,
            ItemStack.with(Items.copper, 80, Items.silicon, 30, iron, 20)
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