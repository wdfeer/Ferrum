package ferrum

import arc.func.Prov
import arc.math.Angles
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.util.Tmp
import mindustry.Vars
import mindustry.gen.Building
import mindustry.gen.Groups
import mindustry.world.blocks.defense.turrets.ReloadTurret

private class CoatingTurret(build: CoatingTurret.() -> ReloadTurretBuild) : ReloadTurret("coating-turret") {
    init {
        this.buildType = Prov { build() }
    }

    val timerTarget: Int = timers++
    val retargetTime: Float = 10f
    val shootCone: Float = 4f
}

fun getCoatingTurret(): ReloadTurret = CoatingTurret {
    object : ReloadTurret.ReloadTurretBuild() {
        var target: Building? = null

        override fun updateTile() {
            //retarget
            if (timer(timerTarget, retargetTime)) {
                target = Groups.build.intersect(x - range, y - range, range * 2, range * 2)
                    .min({ it.team == this.team }) { it.dst(this) }
            }

            //pooled blocks
            if (target != null && !target!!.isAdded) {
                target = null
            }

            //look at target
            if (target != null && target!!.within(
                    this, range
                ) && target!!.team !== team && target.type() != null && target.type().hittable
            ) {
                val dest = angleTo(target)
                rotation = Angles.moveToward(rotation, dest, rotateSpeed * edelta())
                reloadCounter += edelta()

                //shoot when possible
                if (Angles.within(rotation, dest, shootCone) && reloadCounter >= reload) {
                    val realDamage: Float = bulletDamage * Vars.state.rules.blockDamage(team)
                    if (target.damage() > realDamage) {
                        target!!.damage(target.damage() - realDamage)
                    } else {
                        target!!.remove()
                    }

                    beamEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color, Vec2().set(target))
                    shootEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color)
                    hitEffect.at(target!!.x, target!!.y, color)
                    shootSound.at(x + Tmp.v1.x, y + Tmp.v1.y, Mathf.random(0.9f, 1.1f))
                }
            }
        }
    }
}