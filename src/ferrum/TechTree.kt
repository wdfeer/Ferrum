package ferrum

import arc.struct.Seq
import mindustry.content.Blocks
import mindustry.content.Items
import mindustry.content.SectorPresets
import mindustry.content.TechTree.TechNode
import mindustry.ctype.UnlockableContent
import mindustry.game.Objectives.*
import mindustry.type.Item
import mindustry.type.ItemStack

fun Ferrum.modifyFerrumTechTree() {
    fun UnlockableContent.setSelfProduceTechNode(parent: UnlockableContent) {
        techNode = TechNode(parent.techNode, this, emptyArray<ItemStack>()).also {
            it.objectives = Seq.with(Produce(this))
        }
    }

    // Items, Liquids
    pyrite.setSelfProduceTechNode(Items.coal)
    iron.setSelfProduceTechNode(pyrite)
    steel.setSelfProduceTechNode(iron)
    mischmetal.setSelfProduceTechNode(steel)
    so2.setSelfProduceTechNode(pyrite)
    h2so4.setSelfProduceTechNode(so2)

    // Drills
    smartDrill.apply {
        researchCost = ItemStack.with(Items.copper, 300, Items.silicon, 50)
        alwaysUnlocked = false
        techNode = TechNode(Blocks.pneumaticDrill.techNode, this, researchCost).also {
            it.objectives = Seq.with(SectorComplete(SectorPresets.frozenForest), Research(Items.silicon))
        }
    }
    traceDrill.apply {
        researchCost = ItemStack.with(Items.copper, 2500, Items.silicon, 1000, Items.thorium, 500)
        alwaysUnlocked = false
        techNode = TechNode(smartDrill.techNode, this, researchCost).also {
            it.objectives = Seq.with(Research(Items.titanium), Research(Items.thorium))
        }
    }

    // Crafters
    ironworks.apply {
        researchCost = ItemStack.with(Items.lead, 1000, Items.graphite, 500, pyrite, 100)
        alwaysUnlocked = false
        techNode = TechNode(Blocks.graphitePress.techNode, this, researchCost).also {
            it.objectives = Seq.with(Research(pyrite))
        }
    }
    steelForge.apply {
        researchCost = ItemStack.with(Items.copper, 3000, Items.graphite, 1000, iron, 500)
        alwaysUnlocked = false
        techNode = TechNode(ironworks.techNode, this, researchCost).also {
            it.objectives = Seq.with(Research(iron))
        }
    }
    h2so4Plant.apply {
        researchCost = ItemStack.with(Items.copper, 10000, pyrite, 3000, steel, 1000)
        alwaysUnlocked = false
        techNode = TechNode(steelForge.techNode, this, researchCost).also {
            it.objectives = Seq.with(Research(steel), Research(so2))
        }
    }
    steelConverter.apply {
        researchCost = ItemStack.with(Items.copper, 15000, pyrite, 10000, Items.plastanium, 2000, steel, 2000)
        alwaysUnlocked = false
        techNode = TechNode(h2so4Plant.techNode, this, researchCost).also {
            it.objectives = Seq.with(Research(steel), Research(h2so4), Research(Items.plastanium))
        }
    }

    // Turrets
    canna.apply {
        researchCost = ItemStack.with(iron, 150)
        alwaysUnlocked = false
        techNode = TechNode(Blocks.hail.techNode, this, researchCost)
    }
    clyster.apply {
        researchCost = ItemStack.with(Items.metaglass, 500, iron, 80)
        alwaysUnlocked = false
        techNode = TechNode(canna.techNode, this, researchCost)
    }
    flak.apply {
        researchCost = ItemStack.with(Items.lead, 8000, Items.silicon, 3000, iron, 1500, Items.titanium, 500)
        alwaysUnlocked = false
        techNode = TechNode(Blocks.scatter.techNode, this, researchCost).also {
            it.objectives = Seq.with(Research(iron), Research(Items.titanium))
        }
    }
    mitraille.apply {
        researchCost = ItemStack.with(Items.titanium, 4000, steel, 3000)
        alwaysUnlocked = false
        techNode = TechNode(canna.techNode, this, researchCost)
    }
    houf.apply {
        researchCost = ItemStack.with(Items.titanium, 4000, steel, 3000)
        alwaysUnlocked = false
        techNode = TechNode(mitraille.techNode, this, researchCost)
    }
    spark.apply {
        researchCost = ItemStack.with(Items.surgeAlloy, 1000, mischmetal, 1000)
        alwaysUnlocked = false
        techNode = TechNode(Blocks.scorch.techNode, this, researchCost).also {
            it.objectives = Seq.with(Research(mischmetal), Research(Items.surgeAlloy))
        }
    }
    krupp.apply {
        researchCost = ItemStack.with(Items.titanium, 20000, steel, 10000, Items.surgeAlloy, 1000)
        alwaysUnlocked = false
        techNode = TechNode(canna.techNode, this, researchCost).also {
            it.objectives = Seq.with(Research(steel), Research(Items.surgeAlloy))
        }
    }
    gustav.apply {
        researchCost = ItemStack.with(Items.copper, 100000, Items.lead, 50000, Items.titanium, 30000, steel, 20000)
        alwaysUnlocked = false
        techNode = TechNode(canna.techNode, this, researchCost).also {
            it.objectives = Seq.with(Research(steel), SectorComplete(SectorPresets.navalFortress))
        }
    }

    // Other
    ceriumOverdriver.apply {
        researchCost = ItemStack.with(Items.lead, 6000, Items.silicon, 2000, Items.phaseFabric, 1000, steel, 2000, mischmetal, 500)
        alwaysUnlocked = false
        techNode = TechNode(Blocks.overdriveDome.techNode, this, researchCost).also {
            it.objectives = Seq.with(Research(mischmetal))
        }
    }
    ironDome.apply {
        researchCost = ItemStack.with(Items.thorium, 15000, Items.titanium, 15000, steel, 15000, mischmetal, 10000, Items.metaglass, 5000)
        alwaysUnlocked = false
        techNode = TechNode(Blocks.segment.techNode, this, researchCost).also {
            it.objectives = Seq.with(Research(mischmetal), SectorComplete(SectorPresets.nuclearComplex))
        }
    }
}

fun Ferrum.modifyVanillaTechTree() {
    fun UnlockableContent.addReq(amount: Int, item: Item) {
        techNode.apply {
            requirements = requirements.plus(ItemStack(item, amount))
            if (finishedRequirements.size < requirements.size) finishedRequirements =
                finishedRequirements.plus(ItemStack(item, 0))
        }
    }

    Blocks.powerNodeLarge.addReq(100, pyrite)
    Blocks.solarPanel.addReq(100, pyrite)
    Blocks.pyratiteMixer.addReq(200, pyrite)
    Blocks.batteryLarge.addReq(200, pyrite)
    Blocks.largeSolarPanel.addReq(800, pyrite)
    Blocks.multiplicativeReconstructor.addReq(1000, pyrite)
    Blocks.foreshadow.addReq(5000, pyrite)

    Blocks.steamGenerator.addReq(150, iron)
    Blocks.laserDrill.apply {
        techNode.parent = smartDrill.techNode
        addReq(50, iron)
    }
    Blocks.thoriumReactor.addReq(500, iron)
    Blocks.exponentialReconstructor.addReq(5000, iron)

    Blocks.blastDrill.addReq(500, steel)
    Blocks.spectre.addReq(3000, steel)
    Blocks.meltdown.addReq(3000, steel)
    Blocks.tetrativeReconstructor.addReq(10000, steel)

    Blocks.lancer.addReq(100, mischmetal)
    Blocks.parallax.addReq(200, mischmetal)
    Blocks.meltdown.addReq(1000, mischmetal)
    Blocks.foreshadow.addReq(1000, mischmetal)
}
