package ferrum

import arc.graphics.Color
import mindustry.mod.Mod
import mindustry.type.Item
import mindustry.world.blocks.environment.OreBlock

class Ferrum : Mod(){
    override fun loadContent(){
        TODO("Create content")
        iron = Item("iron", Color.valueOf("7f786e")).apply {
            hardness = 3
            cost = 1f
        }
    }
}
