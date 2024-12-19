package ferrum

import mindustry.mod.Mod
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.world.Block
import mindustry.world.blocks.defense.OverdriveProjector
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.environment.OreBlock
import mindustry.world.blocks.production.Drill
import mindustry.world.blocks.production.GenericCrafter

class Ferrum : Mod() {
    lateinit var iron: Item
    lateinit var pyrite: Item
    lateinit var steel: Item
    lateinit var mischmetal: Item
    lateinit var so2: Liquid
    lateinit var h2so4: Liquid

    lateinit var smartDrill: Drill
    lateinit var traceDrill: Drill

    lateinit var ironworks: GenericCrafter
    lateinit var steelForge: GenericCrafter
    lateinit var h2so4Plant: GenericCrafter
    lateinit var steelConverter: GenericCrafter

    lateinit var canna: ItemTurret
    lateinit var clyster: ItemTurret
    lateinit var flak: ItemTurret
    lateinit var houf: ItemTurret
    lateinit var spark: ItemTurret
    lateinit var gustav: ItemTurret
    lateinit var krupp: ItemTurret

    lateinit var oreIron: OreBlock

    lateinit var ceriumOverdriver: OverdriveProjector
    lateinit var coatingTurret: Block

    override fun loadContent() {
        loadItems()
        loadLiquids()
        loadBlocks()
        modifyFerrumTechTree()
        modifyVanillaContent()
    }
}
