package com.example.greenchef.Objects

import android.content.Context
import android.content.SharedPreferences

object RecipeLocalTime {
    private const val PREF_NAME = "RecipePreferences"
    private const val KEY_LAST_UPDATED = "recipe_last_updated"
    const val LAST_UPDATED = "lastUpdated"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setLocalLastUpdated(context: Context, timestamp: Long) {
        val editor = getSharedPreferences(context).edit()
        editor.putLong(KEY_LAST_UPDATED, timestamp)
        editor.apply()
    }

    fun getLocalLastUpdated(context: Context): Long {
        return getSharedPreferences(context).getLong(KEY_LAST_UPDATED, 0L)
    }
}