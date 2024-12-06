package ferrum

import arc.graphics.Color
import mindustry.content.Items
import mindustry.type.Item
import mindustry.world.blocks.environment.OreBlock

fun Ferrum.loadItems() {
    nickel = Item("nickel", Color.valueOf("eccd9e")) // TODO: get color

    pyrite = Item("pyrite", Color.valueOf("eccd9e")).apply {
        cost = 0.8f
        hardness = Items.coal.hardness
    }

    iron = Item("iron", Color.valueOf("7f786e")).apply {
        hardness = 3
    }

    steel = Item("steel", Color.valueOf("#7f7f7f")).apply {
        cost = 1.2f
    }

    oreIron = OreBlock(iron)
}