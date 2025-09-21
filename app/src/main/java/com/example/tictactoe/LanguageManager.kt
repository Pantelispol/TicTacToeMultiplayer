package com.example.tictactoe

import android.content.Context
import android.content.res.Configuration
import android.preference.PreferenceManager
import java.util.*

object LanguageManager {

    private const val SELECTED_LANGUAGE = "language"

    fun setLanguage(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        // Save selected language preference
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putString(SELECTED_LANGUAGE, languageCode).apply()
    }

    fun loadLanguage(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val language = preferences.getString(SELECTED_LANGUAGE, "")
        language?.let {
            setLanguage(context, it)
        }
    }
}
