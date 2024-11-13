package ferrum

import arc.graphics.Color
import mindustry.content.Blocks
import mindustry.mod.Mod
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.blocks.environment.OreBlock

class Ferrum : Mod(){
    lateinit var oreIron: OreBlock
    lateinit var iron: Item

    override fun loadContent(){
        iron = Item("iron", Color.valueOf("7f786e")).apply {
            hardness = 3
            cost = 1f
        }

        oreIron = OreBlock(iron).apply {
            oreDefault = true
            oreThreshold = 0.85f
            oreScale = 25f
        }

        Blocks.steamGenerator.apply {
            requirements = requirements.plus(ItemStack(iron, 15))
        }
        Blocks.thoriumReactor.apply {
            requirements = requirements.plus(ItemStack(iron, 100))
        }

        Blocks.blastDrill.apply {
            requirements = requirements.plus(ItemStack(iron, 40))
        }

        Blocks.multiPress.apply {
            requirements = requirements.plus(ItemStack(iron, 25))
        }
        Blocks.plastaniumCompressor.apply {
            requirements = requirements.plus(ItemStack(iron, 30))
        }

        Blocks.meltdown.apply {
            requirements = requirements.plus(ItemStack(iron, 70))
        }
        Blocks.spectre.apply {
            requirements = requirements.plus(ItemStack(iron, 90))
        }
    }
}
