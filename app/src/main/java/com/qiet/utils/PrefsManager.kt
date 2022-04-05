package com.qiet.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by pierre-alexandrevezinet on 17/02/2022.
 *
 */

class PrefsManager constructor(_context: Context) {
    private var pref: SharedPreferences
    private var editor: SharedPreferences.Editor

    // shared pref mode
    var PRIVATE_MODE = 0

    // Shared preferences file name
    private val PREF_NAME = ApplicationConstants.PREF_APPLICATION
    private val LAST_STEP_VALIDATION_ACTIVITY = ""

    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun lastValidationStepActivity(currentStep: String) {
        editor.putString(LAST_STEP_VALIDATION_ACTIVITY, currentStep)
        editor.commit()
    }

    fun setStringInPreferences(key: String, value: String) {
        editor.putString(key, value)
        editor.commit()
    }

    fun setSetOftStringInPreferences(key: String, value: ArrayList<String>) {
        val set = HashSet<String>()
        set.addAll(value)
        editor.putStringSet(key, set)
        editor.commit()
    }

    fun setIntegerInPreferences(key: String, value: Int) {
        editor.putInt(key, value)
        editor.commit()
    }

    fun setBooleanInPreferences(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun getStringFromPreferences(key: String): String {
        return pref.getString(key, "")!!
    }

    fun getSetOfStringFromPreferences(key: String): Set<String> {
        return pref.getStringSet(key, null)!!
    }

    fun getIntegerFromPreferences(key: String): Int {
        return pref.getInt(key, 0)
    }

    fun getBooleanFromPreferences(key: String): Boolean {
        return pref.getBoolean(key, false)
    }

    fun lastValidationStepActivity(): String {
        return pref.getString(LAST_STEP_VALIDATION_ACTIVITY, "")!!
    }

    fun keyExistsInPreferences(key: String): Boolean {
        return pref.contains(key)
    }

    fun clearAllPreferences() {
        editor.clear().commit()
    }
}