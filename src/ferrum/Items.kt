package ferrum

import arc.graphics.Color
import arc.struct.Seq
import mindustry.content.Items
import mindustry.content.TechTree.TechNode
import mindustry.game.Objectives.Produce
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.blocks.environment.OreBlock

fun Ferrum.loadItems() {
    pyrite = Item("pyrite", Color.valueOf("eccd9e")).apply {
        techNode = TechNode(Items.coal.techNode, this, emptyArray<ItemStack>()).also {
            it.objectives = Seq.with(Produce(this))
        }
        cost = 0.8f
        hardness = Items.coal.hardness
    }

    iron = Item("iron", Color.valueOf("7f786e")).apply {
        techNode = TechNode(pyrite.techNode, this, emptyArray<ItemStack>()).also {
            it.objectives = Seq.with(Produce(this))
        }
        hardness = 3
        cost = 1f
    }

    steel = Item("steel", Color.valueOf("#7f7f7f")).apply {
        techNode = TechNode(iron.techNode, this, emptyArray<ItemStack>()).also {
            it.objectives = Seq.with(Produce(this))
        }
        cost = 1.2f
    }
    
    oreIron = OreBlock(iron)
}