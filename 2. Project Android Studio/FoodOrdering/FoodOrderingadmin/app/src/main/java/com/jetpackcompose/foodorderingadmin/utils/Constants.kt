package com.jetpackcompose.foodorderingadmin.utils

object Constants {
    // Firebase Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_CATEGORIES = "categories"
    const val COLLECTION_FOODS = "foods"
    const val COLLECTION_ORDERS = "orders"
    const val COLLECTION_ADMIN_USERS = "admin_users"
    
    // SharedPreferences
    const val PREF_NAME = "FoodOrderingAdminPrefs"
    const val KEY_USER_ID = "userId"
    const val KEY_USER_EMAIL = "userEmail"
    const val KEY_USER_NAME = "userName"
    const val KEY_IS_LOGGED_IN = "isLoggedIn"
    
    // Intent extras
    const val EXTRA_FOOD_ID = "food_id"
    const val EXTRA_CATEGORY_ID = "category_id"
    const val EXTRA_ORDER_ID = "order_id"
    const val EXTRA_IS_EDIT_MODE = "is_edit_mode"
    
    // Request codes
    const val REQUEST_IMAGE_PICK = 1001
    const val REQUEST_IMAGE_CAPTURE = 1002
    
    // Storage paths
    const val STORAGE_FOOD_IMAGES = "food_images"
    const val STORAGE_CATEGORY_IMAGES = "category_images"
    
    // Order settings
    const val DEFAULT_DELIVERY_FEE = 20.0
    const val TAX_PERCENTAGE = 5.0
    
    // Pagination
    const val PAGE_SIZE = 20
}
