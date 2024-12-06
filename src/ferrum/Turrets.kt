package ferrum

import arc.struct.Seq
import arc.util.Log
import arc.util.Time
import mindustry.Vars
import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.StatusEffects
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
            splashDamage = 100f
            splashDamageRadius = 40f
            status = StatusEffects.blasted
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

    houf = object : ItemTurret("houf") {
        init {
            researchCost = ItemStack.with(Items.titanium, 5000, steel, 5000)
            alwaysUnlocked = false
            techNode = TechNode(canna.techNode, this, researchCost)
        }
    }.apply {
        requirements(Category.turret, ItemStack.with(steel, 275))
        ammo(
            iron,
            BasicBulletType(4.2f, 80f).apply {
                knockback = 3f
                lifetime = 50f
                height = 22f
                width = 18f
                pierce = true
                pierceBuilding = true
                pierceCap = 3
                splashDamage = 40f
                splashDamageRadius = 12f
                hitShake = 1.2f
                hitSound = Sounds.explosion
                hitEffect = Fx.explosion
                shootEffect = Fx.shootSmall
                ammoMultiplier = 1f
            },
            Items.blastCompound,
            DynamicExplosionBulletType(4.2f, 25f, 2f).apply {
                lifetime = 50f
                height = 22f
                width = 18f
                splashDamage = 260f
                splashDamageRadius = 56f
                hitShake = 2f
                hitSound = Sounds.dullExplosion
                shootEffect = Fx.shootSmall
                ammoMultiplier = 2f
                reloadMultiplier = 0.6f
                status = StatusEffects.blasted
            },
            Items.surgeAlloy,
            BasicBulletType(4.2f, 100f).apply {
                lifetime = 50f
                height = 22f
                width = 18f
                pierce = true
                pierceBuilding = true
                pierceCap = 3
                splashDamage = 15f
                splashDamageRadius = 20f
                lightning = 4
                lightningDamage = 15f
                hitShake = 1.2f
                hitSound = Sounds.explosion
                hitEffect = Fx.explosion
                shootEffect = Fx.shootSmall
                ammoMultiplier = 5f
            },
        )
        size = 2
        reload = 100f
        recoil = 1.5f
        shake = 0.8f
        range = (Blocks.lancer as Turret).range * 1.1f
        inaccuracy = 2.25f
        shootCone = 3.5f
        scaledHealth = 350f
        shootSound = Sounds.mediumCannon
        drawer = DrawTurret().apply {
            parts.add(RegionPart("-mid").apply {
                progress = DrawPart.PartProgress.recoil
                under = false
                moveY = -1f
            })
        }
        limitRange(1f)
        coolant = consumeCoolant(0.2f)
    }

    krupp = object : ItemTurret("krupp") {
        init {
            researchCost = ItemStack.with(Items.titanium, 20000, steel, 10000, Items.surgeAlloy, 1000)
            alwaysUnlocked = false
            techNode = TechNode(canna.techNode, this, researchCost).also {
                it.objectives = Seq.with(Produce(steel), Produce(Items.surgeAlloy))
            }
        }
    }.apply {
        requirements(
            Category.turret, ItemStack.with(Items.titanium, 1000, Items.surgeAlloy, 500, steel, 1000)
        )
        ammo(iron, DynamicExplosionBulletType(10f, 500f, 4f).apply {
            shootEffect = Fx.blastsmoke
            hitShake = 4.8f
            width = 18f
            height = 36f
            splashDamage = 1000f
            splashDamageRadius = 50f
            ammoMultiplier = 1f
        }, Items.thorium, DynamicExplosionBulletType(15f, 1000f, 3f).apply {
            shootEffect = Fx.shootBigSmoke
            hitShake = 2.4f
            width = 18f
            height = 36f
            pierceArmor = true
            pierce = true
            pierceBuilding = true
            pierceCap = 7
            ammoMultiplier = 1f
        }, Items.pyratite, DynamicExplosionBulletType(10f, 800f, 4.5f).apply {
            shootEffect = Fx.fireSmoke
            hitShake = 5f
            width = 18f
            height = 36f
            splashDamage = 50f
            splashDamageRadius = 128f
            splashDamagePierce = true
            ammoMultiplier = 1f
            incendAmount = 100
            incendSpread *= 10f
            status = StatusEffects.burning
        }, Items.blastCompound, DynamicExplosionBulletType(10f, 100f, 5f).apply {
            shootEffect = Fx.blastsmoke
            hitShake = 6f
            width = 18f
            height = 36f
            splashDamage = 2000f
            splashDamageRadius = 100f
            ammoMultiplier = 1f
            reloadMultiplier = 0.8f
            status = StatusEffects.blasted
        }, Items.surgeAlloy, DynamicExplosionBulletType(20f, 1000f, 4f).apply {
            shootEffect = Fx.blastsmoke
            hitShake = 4.8f
            width = 18f
            height = 36f
            splashDamage = 800f
            splashDamageRadius = 50f
            lightning = 4
            lightningDamage = 250f
            lightningLength = 12
            ammoMultiplier = 2f
        })

        range = (Blocks.foreshadow as Turret).range * 0.95f
        maxAmmo = 60
        ammoPerShot = 30
        rotateSpeed = 0.67f
        reload = Time.toSeconds * 25f
        ammoUseEffect = Fx.casing2
        recoil = 1.8f
        cooldownTime = reload
        shake = 2.4f
        inaccuracy = 0.2f
        shootCone = 1f
        size = 4
        shootSound = Sounds.largeCannon
        health = 3000
        limitRange(3f)
        coolantMultiplier = 0.25f
        coolant = consumeCoolant(1.2f)
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
            if (timeSinceCompute.inWholeMilliseconds > computeIntervalMillis) computePlaceable()

            return super.isPlaceable() && placeable
        }
    }.apply {
        requirements(
            Category.turret, ItemStack.with(Items.copper, 12000, Items.lead, 11000, steel, 10000, Items.titanium, 4000)
        )
        ammo(iron, DynamicExplosionBulletType(15f, 3000f, 6.5f).apply {
            lifetime = 400f
            shootEffect = Fx.blastsmoke
            hitShake = 24f
            width = 24f
            height = 48f
            splashDamage = 4000f
            splashDamageRadius = 120f
            fragBullets = 32
            fragBullet = BasicBulletType(9f, 200f).apply {
                width = 12f
                height = 21f
                shootEffect = Fx.blastsmoke

                lifetime = 48f
                pierce = true
                pierceBuilding = true
                pierceCap = 6
            }
            ammoMultiplier = 1f
        }, Items.blastCompound, DynamicExplosionBulletType(15f, 100f, 8f).apply {
            lifetime = 400f
            shootEffect = Fx.blastsmoke
            hitEffect = Fx.bigShockwave
            hitShake = 30f
            width = 20f
            height = 40f
            splashDamage = 10000f
            splashDamageRadius = 200f
            splashDamagePierce = true
            ammoMultiplier = 1f
            status = StatusEffects.blasted
        }, Items.surgeAlloy, DynamicExplosionBulletType(30f, 6000f, 7f).apply {
            lifetime = 400f
            shootEffect = Fx.blastsmoke
            hitEffect = Fx.bigShockwave
            hitShake = 30f
            width = 20f
            height = 40f
            splashDamage = 2000f
            splashDamageRadius = 120f
            lightning = 20
            lightningDamage = 200f
            lightningLength = 50
            fragBullets = 10
            fragBullet = BasicBulletType(36f, 200f).apply {
                width = 12f
                height = 21f
                shootEffect = Fx.lightningShoot

                lifetime = 96f
                pierce = true
                pierceBuilding = true
                pierceCap = 3

                lightning = 2
                lightningDamage = 50f
                lightningLength = 30
            }
            ammoMultiplier = 1f
        })

        range = (Blocks.foreshadow as Turret).range * 1.5f
        maxAmmo = 200
        ammoPerShot = 100
        rotateSpeed = 0.33f
        reload = Time.toSeconds * 100f
        ammoUseEffect = Fx.casing4
        recoil = 7f
        cooldownTime = reload
        shake = 14f
        size = 5
        shootCone = 0.5f
        shootSound = Sounds.largeCannon
        health = 8000
        limitRange(6f)
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

private class DynamicExplosionBulletType(speed: Float, damage: Float, val explosionPower: Float) :
    BasicBulletType(speed, damage) {
    override fun hit(b: Bullet?, x: Float, y: Float) {
        // Rotation is the intensity for Fx.dynamicExplosion
        Fx.dynamicExplosion.at(x, y, explosionPower, hitColor)
        super.hit(b, x, y)
    }
}
