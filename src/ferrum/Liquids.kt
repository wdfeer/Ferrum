package ferrum

import arc.graphics.Color
import mindustry.content.Liquids
import mindustry.content.StatusEffects
import mindustry.type.Liquid

fun Ferrum.loadLiquids() {
    so2 = Liquid("so2", Color.valueOf("#e6e6fa")).apply {
        coolant = true
        heatCapacity = (Liquids.cryofluid.heatCapacity * 2f + Liquids.water.heatCapacity) / 3f
        viscosity = 0f

//        gas = true //making it a gas disallows it from being a coolant
        // vanilla gas properties
        boilPoint = -1f
        color = color.cpy()
        color.a = 0.6f
        gasColor = color
        barColor = color.cpy().a(1f)
    }

    h2so4 = Liquid("h2so4", Color.valueOf("#fffacd")).apply {
        effect = StatusEffects.corroded
        boilPoint = 0.9f
        coolant = false
        viscosity = 0.7f
        gasColor = Color.valueOf("#efeabd")
    }
}