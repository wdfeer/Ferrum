package ferrum.util

import mindustry.gen.Groups
import mindustry.world.Block

fun Block.noneBuilt(): Boolean {
    return Groups.build.none { it.block == this }
}