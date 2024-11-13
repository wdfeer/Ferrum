package ferrum

import arc.graphics.Color
import mindustry.content.Blocks
import mindustry.content.Items
import mindustry.entities.bullet.BasicBulletType
import mindustry.gen.Sounds
import mindustry.mod.Mod
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.environment.OreBlock

class Ferrum : Mod() {
    lateinit var oreIron: OreBlock
    lateinit var iron: Item
    lateinit var canna: ItemTurret

    override fun loadContent() {
        iron = Item("iron", Color.valueOf("7f786e")).apply {
            hardness = 3
            cost = 1f
        }

        oreIron = OreBlock(iron).apply {
            oreDefault = true
            oreThreshold = 0.85f
            oreScale = 25f
        }

        canna = ItemTurret("canna").apply {
            requirements(Category.turret, ItemStack.with(iron, 35))
            ammo(
                Items.lead, BasicBulletType(2.5f, 31f).apply {
                    knockback = 1.6f
                    lifetime = 60f
                    height = 14f
                    width = height
                    pierce = true
                    pierceCap = 2
                    reloadMultiplier = 1.2f
                }, iron, BasicBulletType(3.5f, 45f).apply {
                    knockback = 1.6f
                    lifetime = 50f
                    height = 14f
                    width = height
                    pierce = true
                    pierceCap = 3
            },)
            targetAir = false
            reload = 90f
            recoil = 2f
            range = 142f
            inaccuracy = 2.5f
            shootCone = 8f
            health = 360
            shootSound = Sounds.cannon
        }

        modifyVanillaContent()
    }

    private fun modifyVanillaContent() {
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
