package com.lollipop.light

import android.graphics.Color

class HSVColor {

    private val hsv = FloatArray(3)

    var hue: Float
        get() = hsv[0]
        set(value) {
            hsv[0] = value
        }

    var saturation: Float
        get() = hsv[1]
        set(value) {
            hsv[1] = value
        }

    var value: Float
        get() = hsv[2]
        set(value) {
            hsv[2] = value
        }

    var hueWeight: Float
        get() {
            return hue / 360F
        }
        set(value) {
            hue = value * 360F
        }

    fun toRGB(): Int {
        return Color.HSVToColor(hsv)
    }

    fun fromRGB(rgb: Int) {
        Color.colorToHSV(rgb, hsv)
    }

}