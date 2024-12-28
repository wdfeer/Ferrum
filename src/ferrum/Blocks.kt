package ferrum

import mindustry.world.blocks.environment.OreBlock

fun Ferrum.loadBlocks() {
    loadDrills()
    loadCrafters()
    loadTurrets()
    loadSupportBlocks()

    oreIron = OreBlock(iron)
}