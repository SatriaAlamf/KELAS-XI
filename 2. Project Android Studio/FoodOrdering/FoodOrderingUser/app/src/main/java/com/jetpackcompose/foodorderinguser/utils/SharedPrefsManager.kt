package com.jetpackcompose.foodorderinguser.utils

import android.content.Context
import android.content.SharedPreferences
import com.jetpackcompose.foodorderinguser.models.User

class SharedPrefsManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    
    fun saveUserData(user: User) {
        editor.apply {
            putString(Constants.PREF_USER_ID, user.id)
            putString(Constants.PREF_USER_EMAIL, user.email)
            putString(Constants.PREF_USER_NAME, user.name)
            putBoolean(Constants.PREF_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun getUserId(): String {
        return sharedPreferences.getString(Constants.PREF_USER_ID, "") ?: ""
    }
    
    fun getUserEmail(): String {
        return sharedPreferences.getString(Constants.PREF_USER_EMAIL, "") ?: ""
    }
    
    fun getUserName(): String {
        return sharedPreferences.getString(Constants.PREF_USER_NAME, "") ?: ""
    }
    
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(Constants.PREF_IS_LOGGED_IN, false)
    }
    
    fun saveSelectedAddress(addressId: String) {
        editor.putString(Constants.PREF_SELECTED_ADDRESS, addressId).apply()
    }
    
    fun getSelectedAddress(): String {
        return sharedPreferences.getString(Constants.PREF_SELECTED_ADDRESS, "") ?: ""
    }
    
    fun clearUserData() {
        editor.clear().apply()
    }
    
    fun logout() {
        editor.apply {
            putBoolean(Constants.PREF_IS_LOGGED_IN, false)
            remove(Constants.PREF_USER_ID)
            remove(Constants.PREF_USER_EMAIL)
            remove(Constants.PREF_USER_NAME)
            apply()
        }
    }
}