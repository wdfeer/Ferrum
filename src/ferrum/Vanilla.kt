package ferrum

import mindustry.content.Blocks
import mindustry.content.Items
import mindustry.entities.bullet.LiquidBulletType
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.defense.turrets.LiquidTurret
import mindustry.world.blocks.power.PowerGenerator
import mindustry.world.blocks.production.GenericCrafter
import kotlin.math.round

fun Ferrum.modifyVanillaContent() {
    // Pyrite
    run {
        (Blocks.solarPanel as PowerGenerator).powerProduction *= 1.2f
        (Blocks.largeSolarPanel as PowerGenerator).powerProduction *= 1.2f

        Blocks.solarPanel.addRequirement(pyrite, 1)
        Blocks.batteryLarge.addRequirement(pyrite, 30)
        Blocks.largeSolarPanel.addRequirement(pyrite, 15)
        Blocks.phaseWeaver.addRequirement(pyrite, 80)
        Blocks.foreshadow.addRequirement(pyrite, 400)
        Blocks.multiplicativeReconstructor.addRequirement(pyrite, 300)
    }

    // Pyratite
    run {
        (Blocks.pyratiteMixer as GenericCrafter).consumeItems(ItemStack(pyrite, 1))
        (Blocks.impactReactor as PowerGenerator).powerProduction *= 1.1f
        (Blocks.differentialGenerator as PowerGenerator).powerProduction *= 1.25f

        fun ItemTurret.buffAmmo(vararg items: Item) {
            ammoTypes.forEach { ammo ->
                if (items.contains(ammo.key)) {
                    ammo.value.apply {
                        damage = round(damage * 1.1f)
                        splashDamage = round(splashDamage * 1.1f)
                    }
                }
            }
        }

        // Buff pyratite and blast compound as ammo
        listOf(
            Blocks.scorch,
            Blocks.hail,
            Blocks.salvo,
            Blocks.ripple,
            Blocks.swarmer,
            Blocks.cyclone,
            Blocks.spectre
        ).filterIsInstance<ItemTurret>().forEach { it.buffAmmo(Items.pyratite, Items.blastCompound) }
    }

    // Iron
    run {
        Blocks.steamGenerator.addRequirement(iron, 15)
        Blocks.laserDrill.addRequirement(iron, 15)
        Blocks.multiPress.addRequirement(iron, 35)
        Blocks.exponentialReconstructor.addRequirement(iron, 600)
    }

    // Sulfuric Acid
    run {
        val bulletDamage = 16f
        (Blocks.wave as LiquidTurret).ammoTypes.put(h2so4, LiquidBulletType(h2so4).apply {
            damage = bulletDamage
            drag = 0.01f
        })
        (Blocks.tsunami as LiquidTurret).ammoTypes.put(h2so4, LiquidBulletType(h2so4).apply {
            damage = bulletDamage
            drag = 0.001f
            lifetime = 49f
            speed = 4f
            knockback = 1.3f
            puddleSize = 8f
            orbSize = 4f
            ammoMultiplier = 0.4f
            statusDuration = 60f * 4f
        })
    }

    // Steel
    run {
        (Blocks.largeSolarPanel as PowerGenerator).powerProduction *= 1.2f

        Blocks.largeSolarPanel.addRequirement(steel, 40)
        Blocks.thoriumReactor.addRequirement(steel, 100)
        Blocks.impactReactor.addRequirement(steel, 500)
        Blocks.blastDrill.addRequirement(steel, 25)
        Blocks.plastaniumCompressor.addRequirement(steel, 30)
        Blocks.tsunami.addRequirement(steel, 100)
        Blocks.meltdown.addRequirement(steel, 200)
        Blocks.spectre.addRequirement(steel, 150)
        Blocks.tetrativeReconstructor.addRequirement(steel, 1600)
        Blocks.coreNucleus.addRequirement(steel, 2000)
    }

    modifyVanillaTechTree()
    byproductifyVanillaDrills()
}

private fun Block.addRequirement(item: Item, amount: Int) {
    requirements = requirements.plus(ItemStack(item, amount))
}