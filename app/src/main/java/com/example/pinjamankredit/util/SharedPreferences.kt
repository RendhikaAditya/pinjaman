package com.example.pinjamankredit.util

import android.content.Context
import android.content.SharedPreferences

private const val prefName = "Reffeuler.pref"

class SharedPreferences(context: Context) {

    private var sharedPref: SharedPreferences =
        context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor

    init {
        editor = sharedPref.edit()
    }

    fun put(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPref.getString(key, defaultValue) ?: defaultValue
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPref.getBoolean(key, defaultValue)
    }

}