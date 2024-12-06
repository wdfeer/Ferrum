package ferrum

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.struct.Seq
import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.content.TechTree.TechNode
import mindustry.entities.Effect
import mindustry.game.Objectives.Produce
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
import mindustry.world.blocks.production.GenericCrafter
import mindustry.world.draw.*

fun Ferrum.loadCrafters() {
    ironworks = GenericCrafter("ironworks").apply {
        researchCost = ItemStack.with(Items.lead, 1000, Items.graphite, 500, pyrite, 100)
        alwaysUnlocked = false
        techNode = TechNode(Blocks.graphitePress.techNode, this, researchCost).also {
            it.objectives = Seq.with(Produce(pyrite))
        }

        buildType = Prov {
            object : GenericCrafter.GenericCrafterBuild() {
                override fun updateTile() {
                    super.updateTile()
                    // Passive damage if so2 full
                    if (liquids[so2] >= liquidCapacity) {
                        damage(maxHealth / 60f / 240f)
                    }
                }
            }
        }

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

    steelForge = GenericCrafter("steel-forge").apply {
        researchCost = ItemStack.with(Items.copper, 3000, Items.graphite, 1000, iron, 500)
        alwaysUnlocked = false
        techNode = TechNode(ironworks.techNode, this, researchCost).also {
            it.objectives = Seq.with(Produce(iron))
        }

        requirements(Category.crafting, ItemStack.with(Items.copper, 150, iron, 20))
        craftEffect = Fx.smeltsmoke
        outputItem = ItemStack(steel, 2)
        craftTime = 240f
        size = 2
        hasLiquids = false
        drawer = DrawMulti(DrawDefault(), DrawGlowRegion())
        ambientSound = Sounds.smelter
        ambientSoundVolume = 0.08f

        consumeItems(*ItemStack.with(Items.coal, 5, iron, 3))
    }

    h2so4Plant = GenericCrafter("h2so4-plant").apply {
        researchCost = ItemStack.with(Items.copper, 10000, pyrite, 3000, steel, 1000)
        alwaysUnlocked = false
        techNode = TechNode(steelForge.techNode, this, researchCost).also {
            it.objectives = Seq.with(Produce(steel), Produce(so2))
        }

        requirements(
            Category.crafting, ItemStack.with(Items.titanium, 200, Items.silicon, 100, Items.metaglass, 100, steel, 100)
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

    steelConverter = GenericCrafter("steel-converter").apply {
        researchCost = ItemStack.with(Items.copper, 15000, pyrite, 10000, Items.plastanium, 2000, steel, 2000)
        alwaysUnlocked = false
        techNode = TechNode(h2so4Plant.techNode, this, researchCost).also {
            it.objectives = Seq.with(Produce(steel), Produce(h2so4), Produce(Items.plastanium))
        }

        requirements(
            Category.crafting, ItemStack.with(
                Items.titanium, 160, Items.silicon, 100, Items.metaglass, 100, steel, 80, Items.plastanium, 80
            )
        )
        craftEffect = Fx.smeltsmoke
        outputItem = ItemStack(steel, 3)
        craftTime = 72f
        size = 3
        hasPower = true
        hasLiquids = true
        drawer = DrawMulti(DrawDefault(), DrawFlame())
        ambientSound = Sounds.smelter
        ambientSoundVolume = 0.1f

        consumeItems(*ItemStack.with(Items.coal, 1, iron, 3))
        consumeLiquids(LiquidStack(h2so4, 0.05f), LiquidStack(Liquids.water, 0.15f))
        consumePower(2.5f)
    }
}