package ferrum

import arc.func.Prov
import mindustry.content.Blocks
import mindustry.entities.bullet.LiquidBulletType
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.blocks.defense.turrets.LiquidTurret
import mindustry.world.blocks.defense.turrets.PowerTurret
import mindustry.world.blocks.defense.turrets.TractorBeamTurret
import mindustry.world.blocks.power.PowerGenerator

fun Ferrum.modifyVanillaContent() {
    // Pyrite
    run {
        (Blocks.solarPanel as PowerGenerator).powerProduction *= 1.2f
        (Blocks.largeSolarPanel as PowerGenerator).powerProduction *= 1.2f

        Blocks.solarPanel.addRequirement(pyrite, 1)
        Blocks.powerNodeLarge.addRequirement(pyrite, 10)
        Blocks.batteryLarge.addRequirement(pyrite, 30)
        Blocks.largeSolarPanel.addRequirement(pyrite, 15)
        Blocks.pyratiteMixer.addRequirement(pyrite, 80)
        Blocks.phaseWeaver.addRequirement(pyrite, 100)
        Blocks.foreshadow.addRequirement(pyrite, 400)
        Blocks.multiplicativeReconstructor.addRequirement(pyrite, 300)
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
        Blocks.thermalGenerator.addRequirement(steel, 60)
        Blocks.thoriumReactor.addRequirement(steel, 150)
        Blocks.impactReactor.addRequirement(steel, 500)
        Blocks.blastDrill.addRequirement(steel, 25)
        Blocks.plastaniumCompressor.addRequirement(steel, 30)
        Blocks.tsunami.addRequirement(steel, 100)
        Blocks.meltdown.addRequirement(steel, 200)
        Blocks.spectre.addRequirement(steel, 150)
        Blocks.tetrativeReconstructor.addRequirement(steel, 1600)
        Blocks.coreNucleus.addRequirement(steel, 2000)
    }

    // Mischmetal
    run {
        Blocks.mendProjector.addRequirement(mischmetal, 30)
        Blocks.forceProjector.addRequirement(mischmetal, 40)
        Blocks.plastaniumCompressor.addRequirement(mischmetal, 50)

        Blocks.lancer.addRequirement(mischmetal, 20)
        (Blocks.lancer as PowerTurret).apply {
            shootType.damage *= 1.25f
        }

        Blocks.parallax.addRequirement(mischmetal, 60)
        (Blocks.parallax as TractorBeamTurret).apply {
            consPower.usage *= 2f
            damage *= 8f
            buildType = Prov { object : TractorBeamTurret.TractorBeamBuild() {
                val baseDamage = damage
                override fun updateTile() {
                    // effectively ignores armor
                    damage = baseDamage + (target?.armor ?: 0f)
                    super.updateTile()
                }
            } }
        }

        Blocks.meltdown.addRequirement(mischmetal, 80)
        Blocks.foreshadow.addRequirement(mischmetal, 120)
    }

    modifyVanillaTechTree()
    byproductifyVanillaDrills()
}

private fun Block.addRequirement(item: Item, amount: Int) {
    requirements = requirements.plus(ItemStack(item, amount))
}