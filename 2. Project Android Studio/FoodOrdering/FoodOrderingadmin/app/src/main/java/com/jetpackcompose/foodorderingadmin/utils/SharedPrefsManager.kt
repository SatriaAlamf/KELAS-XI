package com.jetpackcompose.foodorderingadmin.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    
    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }
    
    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
    
    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }
    
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }
    
    fun saveInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }
    
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }
    
    fun saveLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }
    
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }
    
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
    
    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
    
    // User-specific methods
    fun saveUserData(userId: String, email: String, name: String) {
        saveString(Constants.KEY_USER_ID, userId)
        saveString(Constants.KEY_USER_EMAIL, email)
        saveString(Constants.KEY_USER_NAME, name)
        saveBoolean(Constants.KEY_IS_LOGGED_IN, true)
    }
    
    fun getUserId(): String = getString(Constants.KEY_USER_ID)
    
    fun getUserEmail(): String = getString(Constants.KEY_USER_EMAIL)
    
    fun getUserName(): String = getString(Constants.KEY_USER_NAME)
    
    fun isLoggedIn(): Boolean = getBoolean(Constants.KEY_IS_LOGGED_IN)
    
    fun clearUserData() {
        clear()
    }
}
