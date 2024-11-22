package ferrum

import arc.graphics.Color
import arc.struct.Seq
import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.TechTree.TechNode
import mindustry.game.Objectives.Produce
import mindustry.gen.Sounds
import mindustry.mod.Mod
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.environment.OreBlock
import mindustry.world.blocks.power.SolarGenerator
import mindustry.world.blocks.production.Drill
import mindustry.world.blocks.production.GenericCrafter
import mindustry.world.blocks.units.Reconstructor
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawGlowRegion
import mindustry.world.draw.DrawMulti

class Ferrum : Mod() {
    lateinit var oreIron: OreBlock
    lateinit var iron: Item
    lateinit var pyrite: Item
    lateinit var steel: Item

    lateinit var pyriteExtractor: Drill
    lateinit var ironExtractor: Drill
    lateinit var ironworks: GenericCrafter
    lateinit var steelForge: GenericCrafter
    lateinit var canna: ItemTurret
    lateinit var clyster: ItemTurret
    lateinit var flak: ItemTurret
    lateinit var gustav: ItemTurret

    override fun loadContent() {
        pyrite = Item("pyrite", Color.valueOf("eccd9e")).apply {
            techNode = TechNode(Items.coal.techNode, this, emptyArray<ItemStack>()).also {
                it.objectives = Seq.with(Produce(this))
            }
            cost = 0.8f
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

        addDrills()

        ironworks = object : GenericCrafter("ironworks") {
            init {
                researchCost = ItemStack.with(Items.lead, 1000, Items.graphite, 500, pyrite, 100)
                alwaysUnlocked = false
                techNode = TechNode(Blocks.graphitePress.techNode, this, researchCost).also {
                    it.objectives = Seq.with(Produce(pyrite))
                }
            }
        }.apply {
            requirements(Category.crafting, ItemStack.with(Items.copper, 50, Items.graphite, 25))
            craftEffect = Fx.smeltsmoke
            outputItem = ItemStack(iron, 2)
            craftTime = 90f
            size = 2
            hasPower = true
            hasLiquids = false
            drawer = DrawMulti(DrawDefault(), DrawGlowRegion())
            ambientSound = Sounds.smelter
            ambientSoundVolume = 0.08f

            consumeItems(*ItemStack.with(Items.graphite, 1, pyrite, 2))
            consumePower(0.50f)
        }

        steelForge = object : GenericCrafter("steel-forge") {
            init {
                researchCost = ItemStack.with(Items.copper, 3000, Items.graphite, 1000, iron, 500)
                alwaysUnlocked = false
                techNode = TechNode(ironworks.techNode, this, researchCost).also {
                    it.objectives = Seq.with(Produce(iron))
                }
            }
        }.apply {
            requirements(Category.crafting, ItemStack.with(Items.copper, 150, iron, 20))
            craftEffect = Fx.smeltsmoke
            outputItem = ItemStack(steel, 1)
            craftTime = 120f
            size = 2
            hasLiquids = false
            drawer = DrawMulti(DrawDefault(), DrawGlowRegion())
            ambientSound = Sounds.smelter
            ambientSoundVolume = 0.08f

            consumeItems(*ItemStack.with(Items.coal, 4, iron))
        }

        addTurrets()

        modifyVanillaContent()
    }

    private fun modifyVanillaContent() {
        // Pyrite
        run {
            (Blocks.pyratiteMixer as GenericCrafter).consumeItems(ItemStack(pyrite, 1))
            (Blocks.multiplicativeReconstructor as Reconstructor).consumeItems(ItemStack(pyrite, 60))
            (Blocks.solarPanel as SolarGenerator).powerProduction *= 1.25f
            (Blocks.largeSolarPanel as SolarGenerator).powerProduction *= 1.25f

            fun addPyriteRequirement(block: Block, amount: Int) {
                block.requirements = block.requirements.plus(ItemStack(pyrite, amount))
            }

            addPyriteRequirement(Blocks.solarPanel, 1)
            addPyriteRequirement(Blocks.batteryLarge, 10)
            addPyriteRequirement(Blocks.largeSolarPanel, 15)
            addPyriteRequirement(Blocks.foreshadow, 200)
        }

        // Iron
        run {
            (Blocks.exponentialReconstructor as Reconstructor).consumeItems(ItemStack(iron, 200))

            fun addIronRequirement(block: Block, amount: Int) {
                block.requirements = block.requirements.plus(ItemStack(iron, amount))
            }

            addIronRequirement(Blocks.steamGenerator, 15)
            addIronRequirement(Blocks.thoriumReactor, 100)
            addIronRequirement(Blocks.laserDrill, 15)
            addIronRequirement(Blocks.multiPress, 35)
            addIronRequirement(Blocks.exponentialReconstructor, 300)
        }

        // Steel
        run {
            (Blocks.tetrativeReconstructor as Reconstructor).consumeItems(ItemStack(steel, 600))

            fun addSteelRequirement(block: Block, amount: Int) {
                block.requirements = block.requirements.plus(ItemStack(steel, amount))
            }

            addSteelRequirement(Blocks.impactReactor, 100)
            addSteelRequirement(Blocks.blastDrill, 25)
            addSteelRequirement(Blocks.plastaniumCompressor, 40)
            addSteelRequirement(Blocks.meltdown, 70)
            addSteelRequirement(Blocks.spectre, 90)
            addSteelRequirement(Blocks.tetrativeReconstructor, 800)
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
}
