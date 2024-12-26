package ferrum

import arc.func.Prov
import arc.util.Time
import ferrum.util.noneBuilt
import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.StatusEffects
import mindustry.entities.bullet.BasicBulletType
import mindustry.entities.bullet.BulletType
import mindustry.entities.bullet.FlakBulletType
import mindustry.entities.part.DrawPart
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootAlternate
import mindustry.entities.pattern.ShootPattern
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.defense.turrets.Turret
import mindustry.world.draw.DrawTurret

fun Ferrum.loadTurrets() {
    canna = ItemTurret("canna").apply {
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

    clyster = ItemTurret("clyster").apply {
        requirements(Category.turret, ItemStack.with(iron, 25, Items.graphite, 25))
        ammo(Items.metaglass, BasicBulletType(4f, 14f).apply {
            lifetime = 50f
            height = 14f
            width = height
            fragBullets = 8
            fragBullet = BasicBulletType(6f, 5f).apply {
                lifetime = 12f

                pierce = true
                pierceCap = 2
            }
            reloadMultiplier = 1.3f
        }, pyrite, BasicBulletType(8f, 17f).apply {
            knockback = 1.6f
            lifetime = 30f
            height = 14f
            width = height
            fragBullets = 3
            fragBullet = BasicBulletType(4f, 13f).apply {
                lifetime = 12f
            }
            reloadMultiplier = 1.5f
        }, iron, BasicBulletType(3f, 22f).apply {
            knockback = 1.6f
            lifetime = 60f
            height = 14f
            width = height
            fragBullets = 5
            fragBullet = BasicBulletType(4f, 8f).apply {
                lifetime = 12f

                pierce = true
                pierceCap = 2
            }
        })
        reload = 100f
        recoil = 2f
        range = 142f
        inaccuracy = 4f
        shootCone = 12f
        health = 320
        shootSound = Sounds.cannon
        limitRange(4f)
    }

    flak = ItemTurret("flak").apply {
        requirements(Category.turret, ItemStack.with(iron, 40, Items.titanium, 40, Items.silicon, 40))
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
            pierceArmor = true
            fragBullets = 4
            fragBullet = BasicBulletType(3f, 12f).apply {
                lifetime = 12f
                collidesGround = false
                pierceArmor = true
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
        }, mischmetal, FlakBulletType(6f, 30f).apply {
            lifetime = 40f
            ammoMultiplier = 4f
            shootEffect = Fx.shootSmall
            width = 6f
            height = 8f
            reloadMultiplier = 1.3f
            homingPower = 0.1f
            hitEffect = Fx.flakExplosion
            splashDamage = 65f
            splashDamageRadius = 24f
            status = StatusEffects.burning
            fragBullets = 3
            fragBullet = BasicBulletType(12f, 20f).apply {
                lifetime = 5f
                collidesGround = false
                status = StatusEffects.burning
            }
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

    mitraille = ItemTurret("mitraille").apply {
        requirements(Category.turret, ItemStack.with(Items.titanium, 120, steel, 120))
        ammoTypes = (Blocks.salvo as ItemTurret).ammoTypes.copy().onEach { it.value = it.value.copy() }
            .onEach { it.value.ammoMultiplier /= 2f }.also {
                it[Items.copper].reloadMultiplier = 1.8f
                it[Items.graphite].reloadMultiplier = 1.2f
                it[Items.silicon].reloadMultiplier += 0.1f
                it[Items.thorium].apply {
                    damage = 20f
                    pierce = true
                    pierceCap = 2
                }
            }.apply {
                put(iron, BasicBulletType(4f, 12f).apply {
                    width = 10f
                    height = 13f
                    shootEffect = Fx.shootBig
                    smokeEffect = Fx.shootBigSmoke
                    ammoMultiplier = 1f
                    lifetime = 60f
                    pierceArmor = true
                    fragBullets = 2
                    fragBullet = BasicBulletType(16f, 8f).apply {
                        width = 5f
                        height = 7f
                        lifetime = 3f
                        pierceArmor = true
                    }
                })
                put(Items.surgeAlloy, BasicBulletType(8f, 30f).apply {
                    width = 10f
                    height = 13f
                    shootEffect = Fx.shootBig
                    smokeEffect = Fx.shootBigSmoke
                    ammoMultiplier = 3f
                    lifetime = 60f
                    lightning = 1
                    lightningDamage = 24f
                    lightningLength = 7
                })
                put(mischmetal, BasicBulletType(3.8f, 19f).apply {
                    width = 10f
                    height = 13f
                    shootEffect = Fx.shootBig
                    smokeEffect = Fx.shootBigSmoke
                    ammoMultiplier = 3f
                    lifetime = 60f
                    homingPower = 0.1f
                    status = StatusEffects.burning
                    makeFire = true
                    pierce = true
                    pierceCap = 2
                    reloadMultiplier = 1.25f
                })
            }
        size = 2
        reload = 5f
        shoot = ShootPattern().apply { shots = 2 }
        recoil = 0.5f
        range = (((Blocks.lancer as Turret).range + (Blocks.hail as Turret).range) / 2).let { it - it % 8 }
        inaccuracy = 16f
        shootCone = 24f
        scaledHealth = 350f
        shootSound = Sounds.shootAlt
        drawer = DrawTurret().apply {
            parts.add(RegionPart("-mid").apply {
                progress = DrawPart.PartProgress.recoil
                under = false
                moveY = -1f
            })
        }
        limitRange(1f)
        coolant = consumeCoolant(0.2f)
        buildType = Prov {
            object : ItemTurret.ItemTurretBuild() {
                override fun shoot(type: BulletType?) {
                    super.shoot(type)
                    damagePierce(30f / (type?.damage ?: 10f))
                }
            }
        }
    }

    houf = ItemTurret("houf").apply {
        requirements(Category.turret, ItemStack.with(Items.titanium, 120, steel, 120))
        ammo(
            pyrite,
            BasicBulletType(6.5f, 50f).apply {
                knockback = 3f
                lifetime = 50f
                height = 22f
                width = 18f
                pierce = true
                pierceBuilding = true
                pierceCap = 2
                splashDamage = 50f
                splashDamageRadius = 16f
                hitShake = 1.2f
                hitSound = Sounds.explosion
                hitEffect = Fx.explosion
                shootEffect = Fx.shootSmall
                ammoMultiplier = 1f
                reloadMultiplier = 1.4f
            },
            iron,
            BasicBulletType(4.2f, 90f).apply {
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
            DynamicExplosionBulletType(4.2f, 35f, 2f).apply {
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
            BasicBulletType(6.5f, 100f).apply {
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
            mischmetal,
            DynamicExplosionBulletType(4.2f, 80f, 1.4f).apply {
                lifetime = 50f
                height = 22f
                width = 18f
                splashDamage = 40f
                splashDamageRadius = 36f
                hitShake = 2f
                hitSound = Sounds.dullExplosion
                shootEffect = Fx.shootSmall
                ammoMultiplier = 2f
                reloadMultiplier = 1.25f
                homingPower = 0.1f
                status = StatusEffects.burning
                makeFire = true
                incendAmount = 2
            },
        )
        size = 2
        reload = 100f
        recoil = 1.5f
        shake = 0.8f
        range = mitraille.range
        inaccuracy = 2.25f
        shootCone = 3.5f
        scaledHealth = mitraille.scaledHealth
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

    spark = ItemTurret("spark").apply {
        requirements(Category.turret, ItemStack.with(steel, 80, Items.surgeAlloy, 45, mischmetal, 45))
        ammo(
            Items.pyratite,
            BulletType(6f, 80f).apply {
                ammoMultiplier = 4f
                hitSize = 7f
                lifetime = 18f
                pierce = true
                statusDuration = 60f * 30
                shootEffect = Fx.shootPyraFlame
                hitEffect = Fx.hitFlameSmall
                despawnEffect = Fx.none
                status = StatusEffects.burning
                hittable = false
                reloadMultiplier = 2f
            },
            Items.surgeAlloy,
            BulletType(12f, 100f).apply {
                ammoMultiplier = 6f
                hitSize = 7f
                lifetime = 18f
                statusDuration = 60f * 10
                shootEffect = Fx.shootBigSmoke
                trailInterval = 0.25f
                hitSound = Sounds.explosion
                trailEffect = Fx.hitFlameSmall
                hitEffect = Fx.hitFlamePlasma
                despawnEffect = Fx.hitFlamePlasma
                status = StatusEffects.burning
                hittable = false
                lightning = 4
                lightningDamage = 50f
                lightningLength *= 2
                reloadMultiplier = 2 / 3f
            },
            mischmetal,
            BulletType(6f, 50f).apply {
                ammoMultiplier = 4f
                hitSize = 7f
                lifetime = 18f
                statusDuration = 60f * 10
                trailInterval = 1f
                trailEffect = Fx.hitFlameSmall
                hitSound = Sounds.explosion
                hitEffect = Fx.flakExplosion
                despawnEffect = Fx.hitFlamePlasma
                homingPower = 0.1f
                status = StatusEffects.burning
                hittable = false
                fragBullets = 4
                fragBullet = BulletType(2f, 18f).apply {
                    hitSize = 5f
                    lifetime = 12f
                    pierce = true
                    statusDuration = 60f * 10
                    despawnEffect = Fx.hitFlameSmall
                    status = StatusEffects.burning
                    hittable = false
                }
            },
        )
        size = 2
        recoil = 0f
        reload = 8f
        coolantMultiplier = 2f
        range = 96f
        shootCone = 30f
        ammoUseEffect = Fx.none
        scaledHealth = 400f
        shootSound = Sounds.flame
        coolant = consumeCoolant(0.1f)
        consumePower(2f)
        limitRange(8f)
    }

    krupp = ItemTurret("krupp").apply {
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
        override fun isPlaceable(): Boolean {
            return super.isPlaceable() && noneBuilt()
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
        }, mischmetal, DynamicExplosionBulletType(22.5f, 1600f, 7.5f).apply {
            lifetime = 400f
            shootEffect = Fx.blastsmoke
            hitEffect = Fx.bigShockwave
            hitShake = 30f
            width = 20f
            height = 40f
            splashDamage = 1600f
            splashDamageRadius = 160f
            makeFire = true
            incendAmount = 4
            fragBullets = 48
            fragBullet = BasicBulletType(18f, 50f).apply {
                width = 12f
                height = 21f
                lifetime = 80f
                pierce = true
                pierceBuilding = true
                pierceCap = 4
                homingPower = 0.1f
                status = StatusEffects.burning
                incendAmount = 1
            }
            ammoMultiplier = 1f
            reloadMultiplier = 1.25f
        })

        range = ((Blocks.foreshadow as Turret).range * 1.5f).let { it - it % 8 }
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
