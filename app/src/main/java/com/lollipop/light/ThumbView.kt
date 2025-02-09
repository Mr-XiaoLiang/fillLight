package com.lollipop.light

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import kotlin.math.min

class ThumbView @JvmOverloads constructor(
    context: Context, attr: AttributeSet? = null
) : RoundImageView(context, attr) {

    private val thumbDrawable = ColorThumbDrawable()

    var thumbColor: Int
        set(value) {
            thumbDrawable.contentColor = value
        }
        get() {
            return thumbDrawable.contentColor
        }

    init {
        background?.let { bg ->
            if (bg is ColorDrawable) {
                thumbDrawable.contentColor = bg.color
            }
        }
        background = thumbDrawable

        attr?.let { a ->
            val typedArray = context.obtainStyledAttributes(a, R.styleable.ThumbView)
            try {
                // 读取自定义属性
                val borderColor =
                    typedArray.getColor(R.styleable.ThumbView_thumbBorderColor, Color.WHITE)
                val borderWidth =
                    typedArray.getDimension(R.styleable.ThumbView_thumbBorderWidth, 0f)
                // 设置属性到 thumbDrawable
                thumbDrawable.borderColor = borderColor
                thumbDrawable.borderWidth = borderWidth
            } finally {
                typedArray.recycle()
            }
        }

    }

    private class ColorThumbDrawable : Drawable() {

        val colorPaint = Paint().apply {
            isDither = true
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        var borderColor = Color.WHITE
            set(value) {
                field = value
                invalidateSelf()
            }
        var borderWidth = 0f
            set(value) {
                field = value
                updateRadius()
            }

        var contentColor = Color.WHITE
            set(value) {
                field = value
                invalidateSelf()
            }

        private val fullBounds = RectF()
        private val contentBounds = RectF()

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            updateRadius()
        }

        private fun updateRadius() {
            if (bounds.isEmpty) {
                return
            }
            val fullRadius = (min(bounds.height(), bounds.width()) * 0.5F)
            val centerX = bounds.centerX()
            val centerY = bounds.centerY()
            fullBounds.set(
                centerX - fullRadius,
                centerY - fullRadius,
                centerX + fullRadius,
                centerY + fullRadius,
            )
            val contentRadius = (fullRadius - borderWidth)
            contentBounds.set(
                centerX - contentRadius,
                centerY - contentRadius,
                centerX + contentRadius,
                centerY + contentRadius,
            )
        }

        override fun draw(canvas: Canvas) {
            colorPaint.color = borderColor
            canvas.drawOval(fullBounds, colorPaint)
            colorPaint.color = contentColor
            canvas.drawOval(contentBounds, colorPaint)
        }

        override fun setAlpha(alpha: Int) {
            colorPaint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            colorPaint.colorFilter = colorFilter
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

    }

}