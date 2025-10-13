package com.example.myapplication.utils

/**
 * Constants untuk aplikasi
 */
object Constants {
    
    // Firestore Collections
    const val COLLECTION_BLOGS = "blogs"
    const val COLLECTION_USERS = "users"
    
    // Firebase Storage Paths
    const val STORAGE_BLOG_IMAGES = "blog_images"
    const val STORAGE_PROFILE_IMAGES = "profile_images"
    
    // Intent Extra Keys
    const val EXTRA_BLOG_ID = "blog_id"
    const val EXTRA_USER_ID = "user_id"
    
    // SharedPreferences Keys
    const val PREF_NAME = "BlogAppPrefs"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_USER_NAME = "user_name"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    
    // Request Codes
    const val REQUEST_IMAGE_PICK = 1001
    const val REQUEST_PERMISSION_STORAGE = 1002
    
    // Validation
    const val MIN_PASSWORD_LENGTH = 6
    const val MIN_NAME_LENGTH = 3
    const val MAX_TITLE_LENGTH = 100
    const val MAX_CONTENT_LENGTH = 5000
    
    // UI
    const val SPLASH_DELAY = 3000L // 3 seconds
    const val IMAGE_MAX_SIZE = 1024 * 1024 * 2 // 2MB
    
    // Error Messages
    const val ERROR_NETWORK = "No internet connection. Please check your network."
    const val ERROR_GENERIC = "Something went wrong. Please try again."
    const val ERROR_INVALID_EMAIL = "Please enter a valid email address."
    const val ERROR_INVALID_PASSWORD = "Password must be at least 6 characters."
    const val ERROR_INVALID_NAME = "Name must be at least 3 characters."
    const val ERROR_EMPTY_FIELDS = "Please fill all fields."
    const val ERROR_AUTH_FAILED = "Authentication failed. Please try again."
    const val ERROR_USER_NOT_FOUND = "User not found."
    const val ERROR_PERMISSION_DENIED = "Permission denied."
    const val ERROR_IMAGE_TOO_LARGE = "Image size is too large. Max 2MB."
    
    // Success Messages
    const val SUCCESS_LOGIN = "Login successful!"
    const val SUCCESS_REGISTER = "Registration successful!"
    const val SUCCESS_BLOG_CREATED = "Blog created successfully!"
    const val SUCCESS_BLOG_UPDATED = "Blog updated successfully!"
    const val SUCCESS_BLOG_DELETED = "Blog deleted successfully!"
    const val SUCCESS_LOGOUT = "Logged out successfully!"
}
