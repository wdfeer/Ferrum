package ferrum

import arc.graphics.Color
import arc.struct.Seq
import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.StatusEffects
import mindustry.content.TechTree.TechNode
import mindustry.game.Objectives.Produce
import mindustry.gen.Sounds
import mindustry.mod.Mod
import mindustry.type.*
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.environment.OreBlock
import mindustry.world.blocks.production.Drill
import mindustry.world.blocks.production.GenericCrafter
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawFlame
import mindustry.world.draw.DrawGlowRegion
import mindustry.world.draw.DrawMulti

class Ferrum : Mod() {
    lateinit var oreIron: OreBlock
    lateinit var iron: Item
    lateinit var pyrite: Item
    lateinit var steel: Item
    lateinit var h2so4: Liquid

    lateinit var pyriteExtractor: Drill
    lateinit var ironExtractor: Drill
    lateinit var ironworks: GenericCrafter
    lateinit var steelForge: GenericCrafter
    lateinit var steelConverter: GenericCrafter

    lateinit var canna: ItemTurret
    lateinit var clyster: ItemTurret
    lateinit var flak: ItemTurret
    lateinit var houf: ItemTurret
    lateinit var gustav: ItemTurret
    lateinit var krupp: ItemTurret

    override fun loadContent() {
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

        h2so4 = Liquid("h2so4", Color.valueOf("#fffacd")).apply {
            techNode = TechNode(pyrite.techNode, this, emptyArray<ItemStack>()).also {
                it.objectives = Seq.with(Produce(this))
            }
            effect = StatusEffects.corroded
            boilPoint = 0.9f
            coolant = false
            viscosity = 0.7f
            gasColor = Color.valueOf("#efeabd")
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
            outputLiquid = LiquidStack(h2so4, outputItem.amount / craftTime * 4) // 1 iron = 4 h2so4
            craftTime = 90f
            size = 2
            hasPower = true
            hasLiquids = true
            ignoreLiquidFullness = true
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

            consumeItems(*ItemStack.with(Items.coal, 4, iron, 1))
        }

        steelConverter = object : GenericCrafter("steel-converter") {
            init {
                researchCost = ItemStack.with(Items.copper, 15000, pyrite, 10000, Items.plastanium, 2000, steel, 2000)
                alwaysUnlocked = false
                techNode = TechNode(steelForge.techNode, this, researchCost).also {
                    it.objectives = Seq.with(Produce(steel), Produce(Items.pyratite), Produce(Items.plastanium))
                }
            }
        }.apply {
            requirements(
                Category.crafting,
                ItemStack.with(Items.titanium, 160, Items.silicon, 90, steel, 80, Items.plastanium, 80)
            )
            craftEffect = Fx.smeltsmoke
            outputItem = ItemStack(steel, 5)
            craftTime = 150f
            size = 3
            hasPower = true
            hasLiquids = false
            drawer = DrawMulti(DrawDefault(), DrawFlame())
            ambientSound = Sounds.smelter
            ambientSoundVolume = 0.1f

            consumeItems(*ItemStack.with(Items.pyratite, 2, iron, 5))
            consumePower(2.5f)
        }

        addTurrets()

        modifyVanillaContent()
    }
}
