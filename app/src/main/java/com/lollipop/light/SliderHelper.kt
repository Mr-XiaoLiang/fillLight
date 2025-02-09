package com.lollipop.light

import android.annotation.SuppressLint
import android.view.View

class SliderHelper(
    val group: View,
    val thumb: View,
    private val onProgressChanged: (progress: Float) -> Unit,
) {

    private var currentProgress = 0F

    @SuppressLint("ClickableViewAccessibility")
    private val sliderTouchListener = View.OnTouchListener { _, event ->
        val min = 0F
        val max = group.width - thumb.width
        var left = event.x - (thumb.width / 2f)
        if (left < min) {
            left = min
        }
        if (left > max) {
            left = max.toFloat()
        }
        val progress = left / (group.width - thumb.width)
        currentProgress = progress
        thumb.translationX = left
        onProgressChanged(progress)
        true
    }

    init {
        group.setOnTouchListener(sliderTouchListener)
    }

    fun updateProgress(progress: Float) {
        currentProgress = progress
        postUpdate()
    }

    private fun postUpdate() {
        group.post {
            val p = currentProgress.coerceIn(0F, 1F)
            val left = p * (group.width - thumb.width)
            thumb.translationX = left
        }
    }

}