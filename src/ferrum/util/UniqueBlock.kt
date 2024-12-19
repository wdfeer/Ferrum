package ferrum.util

import mindustry.Vars
import mindustry.gen.Groups
import mindustry.world.blocks.defense.turrets.Turret

// Two versions just for performance testing

fun Turret.noneBuilt1(): Boolean {
    return Vars.world.tiles.none { it.blockID() == id }
}

fun Turret.noneBuilt2(): Boolean {
    return Groups.build.none { it.block == this }
}