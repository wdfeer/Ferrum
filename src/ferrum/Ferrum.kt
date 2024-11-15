package ferrum

import arc.graphics.Color
import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.content.TechTree.TechNode
import mindustry.entities.bullet.BasicBulletType
import mindustry.gen.Sounds
import mindustry.mod.Mod
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.environment.OreBlock
import mindustry.world.blocks.power.SolarGenerator
import mindustry.world.blocks.production.Drill
import mindustry.world.blocks.production.GenericCrafter
import mindustry.world.blocks.units.Reconstructor
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawGlowRegion
import mindustry.world.draw.DrawMulti
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValues

class Ferrum : Mod() {
    lateinit var oreIron: OreBlock
    lateinit var iron: Item
    lateinit var pyrite: Item

    lateinit var pyriteExtractor: Drill
    lateinit var ironExtractor: Drill
    lateinit var ironworks: GenericCrafter
    lateinit var canna: ItemTurret
    lateinit var clyster: ItemTurret

    override fun loadContent() {
        iron = Item("iron", Color.valueOf("7f786e")).apply {
            techNode = TechNode(Items.titanium.techNode, this, emptyArray<ItemStack>())
            hardness = 3
            cost = 1f
        }

        pyrite = Item("pyrite", Color.valueOf("eccd9e")).apply {
            techNode = TechNode(Items.coal.techNode, this, emptyArray<ItemStack>())
            cost = 1f
        }

        oreIron = OreBlock(iron)

        addDrills()

        ironworks = object : GenericCrafter("ironworks") {
            init {
                researchCost = ItemStack.with(Items.lead, 2000, Items.graphite, 500, pyrite, 100)
                alwaysUnlocked = false
                techNode = TechNode(Blocks.graphitePress.techNode, this, researchCost)
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
            consumePower(0.60f)
        }

        addTurrets()

        modifyVanillaContent()
    }

    private fun addDrills() {
        pyriteExtractor = object : Drill("pyrite-extractor") {
            init {
                researchCost = ItemStack.with(Items.copper, 1200, Items.lead, 1000, Items.graphite, 400)
                alwaysUnlocked = false
                techNode = TechNode(Blocks.pneumaticDrill.techNode, this, researchCost)
            }

            override fun canMine(tile: Tile?): Boolean {
                return tile?.drop() == Items.coal
            }

            override fun countOre(tile: Tile?) {
                super.countOre(tile)
                returnItem = pyrite
            }

            override fun setStats() {
                super.setStats()

                stats.remove(Stat.drillTier)
                stats.add(Stat.drillTier, StatValues.drillables(
                    drillTime, hardnessDrillMultiplier,
                    (size * size).toFloat(), drillMultipliers
                ) { it.itemDrop == Items.coal })
            }
        }.apply {
            requirements(
                Category.production,
                ItemStack.with(Items.lead, 100, Items.graphite, 30)
            )
            drillTime = 280f
            size = 3
            hasPower = true
            tier = 4
            updateEffect = Fx.pulverizeMedium
            drillEffect = Fx.mineBig

            consumePower(1.80f)
            consumeLiquid(Liquids.cryofluid, 0.1f).boost()
        }

        ironExtractor = object : Drill("iron-extractor") {
            init {
                researchCost = ItemStack.with(Items.copper, 2000, Items.graphite, 500, Items.titanium, 200, Items.silicon, 200)
                alwaysUnlocked = false
                techNode = TechNode(Blocks.pneumaticDrill.techNode, this, researchCost)
            }

            override fun canMine(tile: Tile?): Boolean {
                return tile?.drop() == Items.titanium
            }

            override fun countOre(tile: Tile?) {
                super.countOre(tile)
                returnItem = iron
            }

            override fun setStats() {
                super.setStats()

                stats.remove(Stat.drillTier)
                stats.add(Stat.drillTier, StatValues.drillables(
                    drillTime, hardnessDrillMultiplier,
                    (size * size).toFloat(), drillMultipliers
                ) { it.itemDrop == Items.titanium })
            }
        }.apply {
            requirements(
                Category.production,
                ItemStack.with(Items.copper, 80, Items.silicon, 30, iron, 20)
            )
            drillTime = 280f
            size = 3
            hasPower = true
            tier = 4
            updateEffect = Fx.pulverizeMedium
            drillEffect = Fx.mineBig

            consumePower(1.60f)
            consumeLiquid(Liquids.cryofluid, 0.1f).boost()
        }
    }

    private fun addTurrets() {
        canna = object : ItemTurret("canna") {
            init {
                researchCost = ItemStack.with(iron, 150)
                alwaysUnlocked = false
                techNode = TechNode(Blocks.hail.techNode, this, researchCost)
            }
        }.apply {
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

        clyster = object : ItemTurret("clyster") {
            init {
                researchCost = ItemStack.with(Items.metaglass, 500, iron, 80)
                alwaysUnlocked = false
                techNode = TechNode(Blocks.hail.techNode, this, researchCost)
            }
        }.apply {
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
        // Pyrite
        run {
            (Blocks.pyratiteMixer as GenericCrafter).consumeItems(ItemStack(pyrite, 1))
            (Blocks.tetrativeReconstructor as Reconstructor).consumeItems(ItemStack(pyrite, 200))
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
            addIronRequirement(Blocks.impactReactor, 100)

            addIronRequirement(Blocks.laserDrill, 15)
            addIronRequirement(Blocks.blastDrill, 25)

            addIronRequirement(Blocks.multiPress, 35)
            addIronRequirement(Blocks.plastaniumCompressor, 40)

            addIronRequirement(Blocks.meltdown, 70)
            addIronRequirement(Blocks.spectre, 90)

            addIronRequirement(Blocks.exponentialReconstructor, 300)
            addIronRequirement(Blocks.tetrativeReconstructor, 800)
        }
    }
}
