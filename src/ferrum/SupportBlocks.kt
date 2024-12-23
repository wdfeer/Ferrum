package ferrum

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.util.Tmp
import ferrum.util.noneBuilt
import mindustry.content.Blocks
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.entities.Effect
import mindustry.entities.Effect.EffectContainer
import mindustry.gen.Bullet
import mindustry.gen.Groups
import mindustry.gen.Sounds
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.blocks.defense.OverdriveProjector
import mindustry.world.blocks.defense.turrets.PointDefenseTurret
import mindustry.world.blocks.defense.turrets.Turret

fun Ferrum.loadSupportBlocks() {
    ceriumOverdriver = OverdriveProjector("cerium-overdriver").apply {
        requirements(
            Category.effect, ItemStack.with(
                steel, 160, Items.silicon, 130, Items.phaseFabric, 80, mischmetal, 80
            )
        )
        consumePower(15f)
        size = 2
        range /= 2f
        speedBoost += 0.5f
        useTime /= 3f
        consumeItem(mischmetal).boost()
    }

    ironDome = object : PointDefenseTurret("iron-dome") {
        override fun isPlaceable(): Boolean {
            return super.isPlaceable() && noneBuilt()
        }
    }.apply {
        buildType = Prov {
            object : PointDefenseTurret.PointDefenseBuild() {
                val hitSound = Sounds.plasmaboom
                val splashRadius = 80f
                override fun updateTile() {
                    target = Groups.bullet.intersect(x - range, y - range, range * 2, range * 2)
                        .select { it.team !== team && it.type().hittable && it.within(this, range) }
                        .max { b: Bullet -> b.damage } ?: return

                    val dest = angleTo(target)
                    rotation = Angles.moveToward(rotation, dest, rotateSpeed * edelta())
                    reloadCounter += edelta()
                    if (Angles.within(rotation, dest, shootCone) && reloadCounter >= reload) attack(target)
                }

                fun attack(target: Bullet) {
                    target.remove()
                    splash(target.x, target.y)

                    Tmp.v1.trns(rotation, shootLength)

                    beamEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color, Vec2().set(target))
                    shootEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color)
                    hitEffect.at(target.x, target.y, color)
                    hitSound.at(target)
                    shootSound.at(x + Tmp.v1.x, y + Tmp.v1.y, Mathf.random(0.9f, 1.1f))
                    reloadCounter = 0f
                }

                fun splash(x: Float, y: Float) {
                    Groups.bullet.intersect(
                        x - splashRadius, y - splashRadius, splashRadius * 2, splashRadius * 2
                    ).select { it.team !== team && it.type().hittable && it.within(target, splashRadius) }
                        .forEach { it.remove() }
                }
            }
        }
        requirements(
            Category.effect, ItemStack.with(
                steel, 8000, Items.silicon, 6000, Items.thorium, 6000, Items.phaseFabric, 2500, mischmetal, 1600
            )
        )
        health = 12000
        range = (Blocks.foreshadow as Turret).range
        hasPower = true
        consumePower(100f)
        consumeLiquid(Liquids.cryofluid, 0.5f)
        hitEffect = Effect(40f, 100f) { e: EffectContainer ->
            Draw.color(Color.white)
            Lines.stroke(e.fout() * 2f)
            val circleRad = 4f + e.finpow() * 65f
            Lines.circle(e.x, e.y, circleRad)

            for (i in 0..3) {
                Drawf.tri(e.x, e.y, 6f, 100f * e.fout(), (i * 90).toFloat())
            }

            for (i in 0..3) {
                Drawf.tri(e.x, e.y, 3f, 35f * e.fout(), (i * 90).toFloat())
            }
            Drawf.light(e.x, e.y, circleRad * 1.6f, Pal.techBlue, e.fout())
        }
        size = 5
        rotateSpeed *= 3f
        reload = 10f
    }
}