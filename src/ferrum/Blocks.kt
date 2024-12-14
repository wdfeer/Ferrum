package ferrum

import mindustry.content.Items
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.blocks.defense.OverdriveProjector
import mindustry.world.blocks.environment.OreBlock

fun Ferrum.loadBlocks() {
    loadDrills()
    loadCrafters()
    loadTurrets()

    oreIron = OreBlock(iron)

    ceriumOverdriver = OverdriveProjector("cerium-overdriver").apply {
        requirements(
            Category.effect, ItemStack.with(
                steel, 160, Items.silicon, 130, Items.phaseFabric, 80, mischmetal, 80
            )
        )
        consumePower(15f)
        size = 2
        range /= 2f
        speedBoost += 0.5f
        consumeItem(mischmetal).boost()
    }
}