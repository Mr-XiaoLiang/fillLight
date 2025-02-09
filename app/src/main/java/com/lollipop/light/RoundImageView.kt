package com.lollipop.light

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min

open class RoundImageView @JvmOverloads constructor(
    context: Context, attr: AttributeSet? = null
) : AppCompatImageView(context, attr) {

    init {
        this.clipToOutline = true
        this.outlineProvider = RoundOutlineProvider()
    }

    private class RoundOutlineProvider : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(
                0,
                0,
                view.width,
                view.height,
                min(view.height, view.width) / 2f
            )
        }
    }

}