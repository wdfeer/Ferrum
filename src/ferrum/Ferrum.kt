package ferrum

import arc.graphics.Color
import mindustry.content.Blocks
import mindustry.mod.Mod
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.Block
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



        fun addIronRequirement(block: Block, amount: Int) {
            block.requirements = block.requirements.plus(ItemStack(iron, amount))
        }

        addIronRequirement(Blocks.steamGenerator, 15)
        addIronRequirement(Blocks.thoriumReactor, 100)

        addIronRequirement(Blocks.blastDrill, 40)

        addIronRequirement(Blocks.multiPress, 25)
        addIronRequirement(Blocks.plastaniumCompressor, 30)

        addIronRequirement(Blocks.meltdown, 70)
        addIronRequirement(Blocks.spectre, 90)
    }
}
