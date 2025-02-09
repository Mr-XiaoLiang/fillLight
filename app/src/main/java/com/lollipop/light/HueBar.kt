package com.lollipop.light

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import kotlin.math.min

class HueBar @JvmOverloads constructor(
    context: Context, attr: AttributeSet? = null
) : RoundImageView(context, attr) {

    companion object {
        const val SATURATION: Float = 0.8F
        const val BRIGHTNESS: Float = 1F
    }

    private val hueDrawable = HueDrawable()

    init {
        background = hueDrawable
    }

    private class HueDrawable : Drawable() {

        private val trackPaint = Paint().apply {
            isDither = true
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        private val hsvColorArray by lazy {
            val hsv = FloatArray(3)
            hsv[1] = SATURATION
            hsv[2] = BRIGHTNESS
            IntArray(361) { index ->
                hsv[0] = index.toFloat()
                Color.HSVToColor(hsv)
            }
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            if (bounds.isEmpty) {
                return
            }
            updateTrackShader(bounds)
        }

        private fun updateTrackShader(bounds: Rect) {
            val radius = min(bounds.height(), bounds.width()) * 0.5F
            val startX = bounds.left + radius
            val endX = bounds.right - radius
            val centerY = bounds.centerY().toFloat()

            trackPaint.shader = LinearGradient(
                startX,
                centerY,
                endX,
                centerY,
                hsvColorArray,
                null,
                Shader.TileMode.CLAMP
            )
        }

        override fun draw(canvas: Canvas) {
            canvas.drawRect(bounds, trackPaint)
        }

        override fun setAlpha(alpha: Int) {
            trackPaint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            trackPaint.colorFilter = colorFilter
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

    }

}