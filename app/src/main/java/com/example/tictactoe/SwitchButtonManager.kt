package com.example.tictactoe

import android.content.Context

object SwitchButtonManager {
    private const val SWITCH_STATE_KEY = "switch_state"

    fun saveSwitchState(context: Context, isChecked: Boolean) {
        val sharedPreferences = context.getSharedPreferences("switch_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(SWITCH_STATE_KEY, isChecked).apply()
    }

    fun loadSwitchState(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("switch_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(SWITCH_STATE_KEY, false)
    }
}