package ferrum

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.struct.Seq
import mindustry.content.*
import mindustry.content.TechTree.TechNode
import mindustry.entities.Effect
import mindustry.game.Objectives.Produce
import mindustry.gen.Sounds
import mindustry.mod.Mod
import mindustry.type.*
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.environment.OreBlock
import mindustry.world.blocks.production.Drill
import mindustry.world.blocks.production.GenericCrafter
import mindustry.world.draw.*

class Ferrum : Mod() {
    lateinit var oreIron: OreBlock
    lateinit var iron: Item
    lateinit var pyrite: Item
    lateinit var steel: Item
    lateinit var so2: Liquid
    lateinit var h2so4: Liquid

    lateinit var pyriteExtractor: Drill
    lateinit var ironExtractor: Drill
    lateinit var ironworks: GenericCrafter
    lateinit var steelForge: GenericCrafter
    lateinit var h2so4Plant: GenericCrafter
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

        so2 = Liquid("so2", Color.valueOf("#e6e6fa")).apply {
            techNode = TechNode(pyrite.techNode, this, emptyArray<ItemStack>()).also {
                it.objectives = Seq.with(Produce(this))
            }
            coolant = true
            heatCapacity = (Liquids.cryofluid.heatCapacity * 2f + Liquids.water.heatCapacity) / 3f
            viscosity = 0f

//            gas = true //making it a gas disallows it from being a coolant
            // vanilla gas properties
            boilPoint = -1f
            color = color.cpy()
            color.a = 0.6f
            gasColor = color
            barColor = color.cpy().a(1f)
        }

        h2so4 = Liquid("h2so4", Color.valueOf("#fffacd")).apply {
            techNode = TechNode(so2.techNode, this, emptyArray<ItemStack>()).also {
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
                buildType = Prov {
                    object : GenericCrafterBuild() {
                        override fun updateTile() {
                            super.updateTile()
                            // Passive damage if so2 full
                            if (liquids[so2] >= liquidCapacity) {
                                damage(maxHealth / 60f / 240f)
                            }
                        }
                    }
                }
            }
        }.apply {
            requirements(Category.crafting, ItemStack.with(Items.copper, 50, Items.graphite, 25))
            craftEffect = Fx.smeltsmoke
            outputItem = ItemStack(iron, 2)
            craftTime = 90f
            outputLiquid = LiquidStack(so2, outputItem.amount / craftTime * 3) // 1 iron = 3 so2
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

        h2so4Plant = object : GenericCrafter("h2so4-plant") {
            init {
                researchCost = ItemStack.with(Items.copper, 10000, pyrite, 3000, steel, 1000)
                alwaysUnlocked = false
                techNode = TechNode(ironworks.techNode, this, researchCost).also {
                    it.objectives = Seq.with(Produce(so2), Produce(so2))
                }
            }
        }.apply {
            requirements(
                Category.crafting,
                ItemStack.with(Items.titanium, 200, Items.silicon, 100, Items.metaglass, 100, steel, 100)
            )
            updateEffectChance *= 2.5f
            updateEffect = Effect(15f) {
                val color = so2.color.cpy()
                Angles.randLenVectors(
                    it.id.toLong(), 2, 1.2f + it.fin() * 1.4f
                ) { x: Float, y: Float ->
                    Draw.color(color, it.color, it.fin())
                    Fill.square(it.x + x, it.y + y, 0.5f + it.fout() * 2f, 45f)
                }
            }
            craftEffect = Fx.smeltsmoke
            outputLiquid = LiquidStack(h2so4, 0.2f)
            craftTime = 150f
            size = 3
            hasPower = true
            hasLiquids = true
            drawer = DrawMulti(
                DrawRegion("-bottom"),
                DrawLiquidRegion(so2).apply { suffix = "-so2" },
                DrawLiquidRegion(h2so4).apply { suffix = "-h2so4" },
                DrawDefault()
            )

            consumeLiquids(LiquidStack(so2, 0.2f), LiquidStack(Liquids.water, 0.2f))
            consumePower(2f)
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
