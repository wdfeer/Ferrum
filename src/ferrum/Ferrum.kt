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
import mindustry.world.blocks.units.Reconstructor

class Ferrum : Mod() {
    lateinit var oreIron: OreBlock
    lateinit var iron: Item
    lateinit var canna: ItemTurret
    lateinit var clyster: ItemTurret

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

        addTurrets()

        modifyVanillaContent()
    }

    private fun addTurrets() {
        canna = ItemTurret("canna").apply {
            requirements(Category.turret, ItemStack.with(iron, 35))
            ammo(
                Items.lead,
                BasicBulletType(2.75f, 31f).apply {
                    knockback = 1.6f
                    lifetime = 64f
                    height = 14f
                    width = height
                    pierce = true
                    pierceCap = 2
                    reloadMultiplier = 1.3f
                    collidesAir = false
                },
                iron,
                BasicBulletType(3.25f, 42f).apply {
                    knockback = 1.6f
                    lifetime = 50f
                    height = 14f
                    width = height
                    pierce = true
                    pierceCap = 3
                    collidesAir = false
                },
            )
            targetAir = false
            reload = 90f
            recoil = 2f
            range = 146f
            inaccuracy = 2.5f
            shootCone = 8f
            health = 360
            shootSound = Sounds.cannon
        }

        clyster = ItemTurret("clyster").apply {
            requirements(Category.turret, ItemStack.with(iron, 25, Items.graphite, 25))
            ammo(
                Items.metaglass,
                BasicBulletType(4f, 14f).apply {
                    lifetime = 50f
                    height = 14f
                    width = height

                    fragBullet = BasicBulletType(6f, 5f).apply {
                        lifetime = 12f

                        pierce = true
                        pierceCap = 2
                    }
                    fragBullets = 7

                    reloadMultiplier = 1.3f
                },
                iron,
                BasicBulletType(3f, 22f).apply {
                    knockback = 1.6f
                    lifetime = 60f
                    height = 14f
                    width = height

                    fragBullet = BasicBulletType(4f, 8f).apply {
                        lifetime = 12f

                        pierce = true
                        pierceCap = 2
                    }
                    fragBullets = 5
                }
            )
            reload = 100f
            recoil = 2f
            range = 142f
            inaccuracy = 4f
            shootCone = 12f
            health = 320
            shootSound = Sounds.cannon
        }
    }

    private fun modifyVanillaContent() {
        (Blocks.exponentialReconstructor as Reconstructor).consumeItems(ItemStack(iron, 200))

        fun addIronRequirement(block: Block, amount: Int) {
            block.requirements = block.requirements.plus(ItemStack(iron, amount))
        }

        addIronRequirement(Blocks.steamGenerator, 15)
        addIronRequirement(Blocks.thoriumReactor, 100)
        addIronRequirement(Blocks.impactReactor, 200)

        addIronRequirement(Blocks.blastDrill, 25)

        addIronRequirement(Blocks.multiPress, 35)
        addIronRequirement(Blocks.plastaniumCompressor, 40)

        addIronRequirement(Blocks.meltdown, 70)
        addIronRequirement(Blocks.spectre, 90)

        addIronRequirement(Blocks.exponentialReconstructor, 300)
        addIronRequirement(Blocks.tetrativeReconstructor, 800)
    }
}
