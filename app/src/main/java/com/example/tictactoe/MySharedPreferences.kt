package com.example.tictactoe

import android.content.Context
import android.content.SharedPreferences

object MySharedPreferences {

    private const val SHARED_PREFS_FILE = "my_shared_prefs"
    private const val KEY_EMAIL = "user_email"

    // Method to save the user's email address to SharedPreferences
    fun saveUserEmail(context: Context, email: String) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_EMAIL, email)
        editor.apply()
    }

    // Method to retrieve the user's email address from SharedPreferences
    fun getUserEmail(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_EMAIL, null)
    }

    // Method to clear the user's email address from SharedPreferences
    fun clearUserEmail(context: Context) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(KEY_EMAIL)
        editor.apply()
    }
}

