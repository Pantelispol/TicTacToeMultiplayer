package com.example.tictactoe

import android.content.Context

object DarkModeManager {
    private const val DARK_MODE_KEY = "dark_mode"

    fun saveDarkModeState(context: Context, isEnabled: Boolean) {
        val sharedPreferences = context.getSharedPreferences("dark_mode_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(DARK_MODE_KEY, isEnabled).apply()
    }

    fun loadDarkModeState(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("dark_mode_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(DARK_MODE_KEY, false)
    }
}
