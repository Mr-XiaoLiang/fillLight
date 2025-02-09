package com.lollipop.light

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.light.databinding.ActivityMainBinding
import com.lollipop.light.databinding.ItemHistoryColorBinding
import java.util.Collections

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var currentHue = 0F
    private var currentSaturation = 1F
    private var currentBrightness = 0.5F

    private val hsvColor = HSVColor()

    private var currentColor = 0

    private val hueSliderHelper by lazy {
        SliderHelper(
            group = binding.hueSlider,
            thumb = binding.hueThumb,
            onProgressChanged = ::onHueChanged
        )
    }

    private val saturationSliderHelper by lazy {
        SliderHelper(
            group = binding.saturationSlider,
            thumb = binding.saturationThumb,
            onProgressChanged = ::onSaturationChanged
        )
    }

    private val brightnessSliderHelper by lazy {
        SliderHelper(
            group = binding.brightnessSlider,
            thumb = binding.brightnessThumb,
            onProgressChanged = ::onBrightnessChanged
        )
    }

    private val backgroundColorDrawable = ColorDrawable()

    private val colorHistoryList = mutableListOf<Int>()

    private val colorHistorySwipeDelegate by lazy {
        ColorHistorySwipeDelegate(
            recyclerView = binding.colorHistoryGroup,
            onSwap = ::onSwapColorHistory,
            onSwipe = ::onSwipeColorHistory
        )
    }

    private val settings by lazy {
        LSettings(this)
    }

    private val colorHistoryAdapter by lazy {
        ColorHistoryAdapter(colorHistoryList) { color ->
            resumeColor(color)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initInsets()
        initConfig()
        initView()
        resumeColor(settings.lastColor)
    }

    private fun initConfig() {
        currentBrightness = settings.lastBrightness
        colorHistoryList.clear()
        colorHistoryList.addAll(settings.loadColorHistory())
    }

    private fun initView() {
        brightnessSliderHelper.updateProgress(currentBrightness)
        binding.backgroundView.background = backgroundColorDrawable
        binding.backgroundView.setOnClickListener {
            updateButtonVisibility()
        }
        binding.pinButton.setOnClickListener {
            pinColor()
        }
        binding.colorHistoryGroup.adapter = colorHistoryAdapter
        binding.colorHistoryGroup.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        colorHistorySwipeDelegate
        updateColorHistory()
    }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainGroup) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun updateButtonVisibility() {
        val visible = !binding.mainGroup.isVisible
        binding.mainGroup.isVisible = visible
        WindowInsetsControllerCompat(window, window.decorView).also {
            if (visible) {
                it.show(
                    WindowInsetsCompat.Type.systemBars()
                )
            } else {
                it.hide(
                    WindowInsetsCompat.Type.systemBars()
                )
            }
        }
    }

    private fun pinColor() {
        colorHistoryList.add(0, currentColor)
        colorHistoryAdapter.notifyItemInserted(0)
        binding.colorHistoryGroup.scrollToPosition(0)
    }

    private fun onSwapColorHistory(from: Int, to: Int): Boolean {
        Collections.swap(colorHistoryList, from, to)
        return true
    }

    private fun onSwipeColorHistory(position: Int): Boolean {
        colorHistoryList.removeAt(position)
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateColorHistory() {
        colorHistoryAdapter.notifyDataSetChanged()
    }

    private fun onHueChanged(progress: Float) {
        currentHue = progress
        updateColor()
    }

    private fun resumeColor(color: Int) {
        currentColor = color
        hsvColor.fromRGB(color)
        currentHue = hsvColor.hueWeight
        currentSaturation = hsvColor.saturation
        hueSliderHelper.updateProgress(currentHue)
        saturationSliderHelper.updateProgress(currentSaturation)
        updateColor()
    }

    private fun onSaturationChanged(progress: Float) {
        currentSaturation = progress
        updateColor()
    }

    private fun onBrightnessChanged(progress: Float) {
        currentBrightness = progress
        updateBrightness()
    }

    private fun updateColor() {
        hsvColor.hueWeight = currentHue
        hsvColor.saturation = currentSaturation
        hsvColor.value = 1F
        val color = hsvColor.toRGB()
        currentColor = color
        backgroundColorDrawable.color = color

        hsvColor.value = 0.8F
        binding.hueThumb.thumbColor = hsvColor.toRGB()
    }

    private fun updateBrightness() {
        val window = window
        val layoutParams = window.attributes
        layoutParams.screenBrightness = currentBrightness
        window.attributes = layoutParams
    }

    override fun onPause() {
        super.onPause()
        settings.lastColor = currentColor
        settings.lastBrightness = currentBrightness
        settings.saveColorHistory(colorHistoryList)
    }

    private class ColorHistoryAdapter(
        private val colorList: List<Int>,
        private val onColorClickCallback: (color: Int) -> Unit
    ) : RecyclerView.Adapter<ColorHistoryHolder>() {

        private var layoutInflater: LayoutInflater? = null

        private fun getLayoutInflater(parent: ViewGroup): LayoutInflater {
            return layoutInflater ?: LayoutInflater.from(parent.context).also {
                layoutInflater = it
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ColorHistoryHolder {
            return ColorHistoryHolder(
                ItemHistoryColorBinding.inflate(getLayoutInflater(parent), parent, false),
                ::onItemClick
            )
        }

        private fun onItemClick(position: Int) {
            if (position < 0 || position >= colorList.size) {
                return
            }
            onColorClickCallback(colorList[position])
        }

        override fun onBindViewHolder(holder: ColorHistoryHolder, position: Int) {
            holder.bind(colorList[position])
        }

        override fun getItemCount(): Int {
            return colorList.size
        }
    }

    private class ColorHistoryHolder(
        private val binding: ItemHistoryColorBinding,
        private val onClickCallback: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val colorDrawable = ColorDrawable()

        init {
            binding.colorView.background = colorDrawable
            binding.colorView.setOnClickListener {
                onColorClick()
            }
        }

        private fun onColorClick() {
            onClickCallback(adapterPosition)
        }

        fun bind(color: Int) {
            colorDrawable.color = color
        }
    }

}