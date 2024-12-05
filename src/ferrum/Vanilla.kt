package ferrum

import mindustry.content.Blocks
import mindustry.content.Items
import mindustry.content.TechTree.TechNode
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

        fun addPyriteRequirement(block: Block, amount: Int) {
            block.requirements = block.requirements.plus(ItemStack(pyrite, amount))
        }

        addPyriteRequirement(Blocks.solarPanel, 1)
        addPyriteRequirement(Blocks.batteryLarge, 30)
        addPyriteRequirement(Blocks.largeSolarPanel, 15)
        addPyriteRequirement(Blocks.phaseWeaver, 80)
        addPyriteRequirement(Blocks.foreshadow, 400)
        addPyriteRequirement(Blocks.multiplicativeReconstructor, 300)
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
        fun addIronRequirement(block: Block, amount: Int) {
            block.requirements = block.requirements.plus(ItemStack(iron, amount))
        }

        addIronRequirement(Blocks.steamGenerator, 15)
        addIronRequirement(Blocks.laserDrill, 15)
        addIronRequirement(Blocks.multiPress, 35)
        addIronRequirement(Blocks.exponentialReconstructor, 600)
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

        fun addSteelRequirement(block: Block, amount: Int) {
            block.requirements = block.requirements.plus(ItemStack(steel, amount))
        }

        addSteelRequirement(Blocks.largeSolarPanel, 40)
        addSteelRequirement(Blocks.thoriumReactor, 100)
        addSteelRequirement(Blocks.impactReactor, 500)
        addSteelRequirement(Blocks.blastDrill, 25)
        addSteelRequirement(Blocks.plastaniumCompressor, 30)
        addSteelRequirement(Blocks.tsunami, 100)
        addSteelRequirement(Blocks.meltdown, 200)
        addSteelRequirement(Blocks.spectre, 150)
        addSteelRequirement(Blocks.tetrativeReconstructor, 1600)
        addSteelRequirement(Blocks.coreNucleus, 2000)
    }

    // Tech Tree
    run {
        fun TechNode.addReq(amount: Int, item: Item) {
            requirements = requirements.plus(ItemStack(item, amount))
            if (finishedRequirements.size < requirements.size)
                finishedRequirements = finishedRequirements.plus(ItemStack(item, 0))
        }

        Blocks.solarPanel.techNode.addReq(50, pyrite)
        Blocks.pyratiteMixer.techNode.addReq(200, pyrite)
        Blocks.batteryLarge.techNode.addReq(200, pyrite)
        Blocks.largeSolarPanel.techNode.addReq(800, pyrite)
        Blocks.multiplicativeReconstructor.techNode.addReq(1000, pyrite)
        Blocks.foreshadow.techNode.addReq(5000, pyrite)


        Blocks.steamGenerator.techNode.addReq(150, iron)
        Blocks.laserDrill.techNode.apply {
            parent = pyriteExtractor.techNode
            addReq(50, iron)
        }
        Blocks.thoriumReactor.techNode.addReq(500, iron)
        Blocks.exponentialReconstructor.techNode.addReq(5000, iron)


        Blocks.blastDrill.techNode.addReq(500, steel)
        Blocks.spectre.techNode.addReq(3000, steel)
        Blocks.meltdown.techNode.addReq(3000, steel)
        Blocks.tetrativeReconstructor.techNode.addReq(10000, steel)
    }
}
