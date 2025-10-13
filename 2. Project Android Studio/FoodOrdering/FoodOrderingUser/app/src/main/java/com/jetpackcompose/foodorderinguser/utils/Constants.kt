package com.jetpackcompose.foodorderinguser.utils

object Constants {
    // Firebase Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_CATEGORIES = "categories"
    const val COLLECTION_FOODS = "foods"
    const val COLLECTION_ORDERS = "orders"
    const val COLLECTION_CART = "cart"
    const val COLLECTION_REVIEWS = "reviews"
    const val COLLECTION_RESTAURANTS = "restaurants"
    
    // Shared Preferences
    const val PREFS_NAME = "FoodOrderingPrefs"
    const val PREF_USER_ID = "user_id"
    const val PREF_USER_EMAIL = "user_email"
    const val PREF_USER_NAME = "user_name"
    const val PREF_IS_LOGGED_IN = "is_logged_in"
    const val PREF_SELECTED_ADDRESS = "selected_address"
    
    // Request Codes
    const val REQUEST_LOCATION_PERMISSION = 100
    const val REQUEST_CAMERA_PERMISSION = 101
    const val REQUEST_STORAGE_PERMISSION = 102
    
    // Intent Extra Keys
    const val EXTRA_FOOD_ID = "food_id"
    const val EXTRA_CATEGORY_ID = "category_id"
    const val EXTRA_ORDER_ID = "order_id"
    const val EXTRA_FOOD_ITEM = "food_item"
    const val EXTRA_CATEGORY_NAME = "category_name"
    
    // Order Status Messages
    val ORDER_STATUS_MESSAGES = mapOf(
        "PENDING" to "Order Placed",
        "CONFIRMED" to "Order Confirmed",
        "PREPARING" to "Preparing Your Order",
        "OUT_FOR_DELIVERY" to "Out for Delivery",
        "DELIVERED" to "Order Delivered",
        "CANCELLED" to "Order Cancelled",
        "REFUNDED" to "Order Refunded"
    )
    
    // Error Messages
    const val ERROR_NETWORK = "Network error. Please check your internet connection."
    const val ERROR_GENERAL = "Something went wrong. Please try again."
    const val ERROR_AUTH = "Authentication failed. Please login again."
    const val ERROR_EMPTY_CART = "Your cart is empty."
    const val ERROR_INVALID_EMAIL = "Please enter a valid email address."
    const val ERROR_WEAK_PASSWORD = "Password should be at least 6 characters."
    
    // Success Messages
    const val SUCCESS_ORDER_PLACED = "Order placed successfully!"
    const val SUCCESS_ITEM_ADDED_TO_CART = "Item added to cart!"
    const val SUCCESS_PROFILE_UPDATED = "Profile updated successfully!"
    const val SUCCESS_ADDRESS_ADDED = "Address added successfully!"
    
    // Delivery Settings
    const val DEFAULT_DELIVERY_FEE = 2.99
    const val FREE_DELIVERY_MINIMUM = 15.00
    const val TAX_RATE = 0.10 // 10%
    
    // Image URLs (Sample)
    const val DEFAULT_FOOD_IMAGE = "https://via.placeholder.com/300x200.png?text=Food+Image"
    const val DEFAULT_CATEGORY_IMAGE = "https://via.placeholder.com/150x150.png?text=Category"
    const val DEFAULT_USER_IMAGE = "https://via.placeholder.com/100x100.png?text=User"
    const val DEFAULT_RESTAURANT_IMAGE = "https://via.placeholder.com/400x200.png?text=Restaurant"
}