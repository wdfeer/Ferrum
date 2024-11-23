package ferrum

import arc.struct.Seq
import arc.util.Log
import arc.util.Time
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.TechTree.TechNode
import mindustry.entities.bullet.BasicBulletType
import mindustry.entities.bullet.FlakBulletType
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootAlternate
import mindustry.game.Objectives.Produce
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.defense.turrets.Turret
import mindustry.world.draw.DrawTurret
import kotlin.time.TimeSource
import kotlin.time.measureTime

fun Ferrum.addTurrets() {
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
            techNode = TechNode(canna.techNode, this, researchCost)
        }
    }.apply {
        requirements(Category.turret, ItemStack.with(iron, 25, Items.graphite, 25))
        ammo(Items.metaglass, BasicBulletType(4f, 14f).apply {
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
        }, iron, BasicBulletType(3f, 22f).apply {
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
        })
        reload = 100f
        recoil = 2f
        range = 142f
        inaccuracy = 4f
        shootCone = 12f
        health = 320
        shootSound = Sounds.cannon
    }

    flak = object : ItemTurret("flak") {
        init {
            researchCost = ItemStack.with(Items.lead, 8000, Items.silicon, 3000, iron, 1500, Items.titanium, 500)
            alwaysUnlocked = false
            techNode = TechNode(Blocks.scatter.techNode, this, researchCost).also {
                it.objectives = Seq.with(Produce(iron), Produce(Items.titanium))
            }
        }
    }.apply {
        requirements(Category.turret, ItemStack.with(iron, 125, Items.titanium, 65, Items.silicon, 50))
        Blocks.scatter
        ammo(pyrite, FlakBulletType(9f, 6f).apply {
            lifetime = 40f
            shootEffect = Fx.shootSmall
            width = 6f
            height = 8f
            hitEffect = Fx.flakExplosion
            splashDamage = 50f
            splashDamageRadius = 8f
            reloadMultiplier = 1.4f
        }, iron, FlakBulletType(6f, 12f).apply {
            lifetime = 40f
            shootEffect = Fx.shootSmall
            width = 6f
            height = 8f
            hitEffect = Fx.flakExplosion
            splashDamage = 60f
            splashDamageRadius = 8f
            fragBullets = 4
            fragBullet = BasicBulletType(3f, 8f).apply {
                lifetime = 12f
                collidesGround = false
            }
        }, Items.blastCompound, FlakBulletType(6f, 5f).apply {
            lifetime = 40f
            ammoMultiplier = 4f
            shootEffect = Fx.shootSmall
            width = 6f
            height = 8f
            hitEffect = Fx.blastExplosion
            splashDamage = 80f
            splashDamageRadius = 32f
        })
        reload = 11f
        recoil = 2f
        range = 200f
        inaccuracy = 7f
        shootCone = 10f
        scaledHealth = 320f
        shootSound = Sounds.shootAlt
        size = 2
        targetGround = false
        coolant = consumeCoolant(0.2f)
        recoils = 2
        shoot = ShootAlternate(7f)
        drawer = DrawTurret().apply {
            for (i in 0..1) {
                parts.add(object : RegionPart("-barrel-" + (if (i == 0) "l" else "r")) {
                    init {
                        progress = PartProgress.recoil
                        recoilIndex = i
                        under = true
                        moveY = -1.5f
                    }
                })
            }
        }
        limitRange(4f)
    }

    gustav = object : ItemTurret("gustav") {
        init {
            researchCost = ItemStack.with(Items.copper, 100000, Items.lead, 50000, Items.titanium, 30000, steel, 20000)
            alwaysUnlocked = false
            techNode = TechNode(canna.techNode, this, researchCost).also {
                it.objectives = Seq.with(Produce(steel))
            }
        }

        var placeable: Boolean = true
        var lastPlaceableComputeTime: TimeSource.Monotonic.ValueTimeMark = TimeSource.Monotonic.markNow()
        fun computePlaceable() {
            measureTime {
                placeable = Vars.world.tiles.none { it.blockID() == id }
            }.takeIf { it.inWholeMilliseconds > 30 }?.let {
                    Log.log(Log.LogLevel.warn, "Took too long to compute whether gustav is placeable! ($it)")
                }

            lastPlaceableComputeTime = TimeSource.Monotonic.markNow()
        }

        val computeIntervalMillis = 2500

        override fun isPlaceable(): Boolean {
            val timeSinceCompute = TimeSource.Monotonic.markNow() - lastPlaceableComputeTime
            if (timeSinceCompute.inWholeMilliseconds > computeIntervalMillis)
                computePlaceable()

            return super.isPlaceable() && placeable
        }
    }.apply {
        requirements(
            Category.turret, ItemStack.with(Items.copper, 12000, Items.lead, 11000, steel, 10000, Items.titanium, 4000)
        )
        ammo(iron, object : BasicBulletType(15f, 3000f) {
            override fun hit(b: Bullet?, x: Float, y: Float) {
                // Rotation is the intensity for Fx.dynamicExplosion
                Fx.dynamicExplosion.at(x, y, 6.5f, hitColor)
                super.hit(b, x, y)
            }
        }.apply {
            lifetime = 400f
            shootEffect = Fx.blastsmoke
            trailEffect = Fx.missileTrail
            hitShake = 24f
            width = 24f
            height = 48f
            splashDamage = 4000f
            splashDamageRadius = 120f
            splashDamagePierce = true
            fragBullets = 32
            fragBullet = BasicBulletType(9f, 200f).apply {
                width = 12f
                height = 21f
                shootEffect = Fx.blastsmoke
                trailEffect = Fx.artilleryTrailSmoke

                lifetime = 48f
                pierce = true
                pierceBuilding = true
                pierceCap = 6
            }
        })

        range = (Blocks.foreshadow as Turret).range * 1.5f
        maxAmmo = 200
        ammoPerShot = 50
        rotateSpeed = 0.33f
        reload = Time.toSeconds * 100f
        ammoUseEffect = Fx.casing4
        recoil = 7f
        cooldownTime = reload
        shake = 14f
        size = 5
        shootSound = Sounds.largeCannon
        health = 8000
        limitRange(2f)
        consumePower(5f)
        targetInterval = Float.POSITIVE_INFINITY
        drawer = DrawTurret().apply {
            parts.add(RegionPart("-mid").apply {
                progress = DrawPart.PartProgress.recoil
                under = false
                moveY = -2.5f
            })
        }
    }
}