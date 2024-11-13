package ferrum

import arc.graphics.Color
import mindustry.mod.Mod
import mindustry.type.Item
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
    }
}
