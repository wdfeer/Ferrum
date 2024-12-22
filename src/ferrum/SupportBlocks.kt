package ferrum

import arc.func.Prov
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.struct.Seq
import arc.util.Tmp
import ferrum.util.noneBuilt
import mindustry.content.Blocks
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.gen.Bullet
import mindustry.gen.Groups
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
                override fun updateTile() {
                    reloadCounter += delta()

                    val targets = Groups.bullet.intersect(x - range, y - range, range * 2, range * 2)
                        .select { it.team !== team && it.type().hittable && it.within(this, range) }

                    if (!targets.isEmpty && reloadCounter >= reload) attack(targets)
                }

                fun attack(targets: Seq<Bullet>) {
                    targets.forEach {
                        it.remove()

                        beamEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color, Vec2().set(it))
                        shootEffect.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color)
                        if (it.damage > 25f) hitEffect.at(it.x, it.y, color)
                        shootSound.at(x + Tmp.v1.x, y + Tmp.v1.y, Mathf.random(0.9f, 1.1f))
                    }
                    reloadCounter = 0f
                }
            }
        }
        requirements(
            Category.effect, ItemStack.with(
                Items.titanium, 8000, steel, 8000, Items.silicon, 6000, Items.phaseFabric, 2500, mischmetal, 1600
            )
        )
        health = 12000
        range = (Blocks.foreshadow as Turret).range
        hasPower = true
        consumePower(100f)
        consumeLiquid(Liquids.cryofluid, 0.5f)
        hitEffect = Fx.smokeCloud
        size = 5
        reload = 10f
    }
}