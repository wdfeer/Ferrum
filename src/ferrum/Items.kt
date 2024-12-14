package ferrum

import arc.graphics.Color
import mindustry.content.Items
import mindustry.type.Item

fun Ferrum.loadItems() {
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

    mischmetal = Item("mischmetal", Color.valueOf("698c69")).apply {
        cost = 2f
    }
}