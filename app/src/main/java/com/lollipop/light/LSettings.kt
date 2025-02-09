package com.lollipop.light

import android.content.Context
import android.util.Log
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class LSettings(
    private val context: Context
) {

    private val preference by lazy {
        context.getSharedPreferences("LSettings", Context.MODE_PRIVATE)
    }

    var lastColor by intValue(0xFF00A48F.toInt())
    var lastBrightness by floatValue(0.5F)
    var colorHistory by stringValue("")

    fun saveColorHistory(color: List<Int>) {
        colorHistory = serializeColorList(color)
    }

    fun loadColorHistory(): List<Int> {
        return deserializeColorList(colorHistory)
    }

    private fun intValue(def: Int) = PreferenceInt(def)
    private fun floatValue(def: Float) = PreferenceFloat(def)
    private fun stringValue(def: String) = PreferenceString(def)

    private class PreferenceInt(
        private val def: Int
    ) : ReadWriteProperty<LSettings, Int> {
        override fun getValue(thisRef: LSettings, property: KProperty<*>): Int {
            return thisRef.preference.getInt(property.name, def)
        }

        override fun setValue(thisRef: LSettings, property: KProperty<*>, value: Int) {
            thisRef.preference.edit().putInt(property.name, value).apply()
        }
    }

    private class PreferenceFloat(
        private val def: Float
    ) : ReadWriteProperty<LSettings, Float> {
        override fun getValue(thisRef: LSettings, property: KProperty<*>): Float {
            return thisRef.preference.getFloat(property.name, def)
        }

        override fun setValue(thisRef: LSettings, property: KProperty<*>, value: Float) {
            thisRef.preference.edit().putFloat(property.name, value).apply()
        }
    }

    private class PreferenceString(
        private val def: String
    ) : ReadWriteProperty<LSettings, String> {
        override fun getValue(thisRef: LSettings, property: KProperty<*>): String {
            return thisRef.preference.getString(property.name, def) ?: def
        }

        override fun setValue(thisRef: LSettings, property: KProperty<*>, value: String) {
            thisRef.preference.edit().putString(property.name, value).apply()
        }
    }

    private fun serializeColorList(list: List<Int>): String {
        if (list.isEmpty()) {
            return ""
        }
        try {
            return list.joinToString(separator = ",", transform = { it.toString(16) })
        } catch (e: Throwable) {
            Log.e("LSettings", "serializeColorList", e)
        }
        return ""
    }

    private fun deserializeColorList(string: String): List<Int> {
        if (string.isEmpty()) {
            return emptyList()
        }
        try {
            return string.split(",").map { it.toInt(16) }
        } catch (e: Throwable) {
            Log.e("LSettings", "deserializeColorList", e)
        }
        return emptyList()
    }

}


