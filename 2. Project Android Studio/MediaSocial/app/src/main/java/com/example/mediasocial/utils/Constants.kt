package com.example.mediasocial.utils

object Constants {
    
    // Firebase Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_STORIES = "stories"
    const val COLLECTION_POSTS = "posts"
    const val COLLECTION_NOTIFICATIONS = "notifications"
    
    // Firebase Storage Paths
    const val STORAGE_PROFILE_IMAGES = "profile_images"
    const val STORAGE_STORIES = "stories"
    const val STORAGE_POSTS = "posts"
    
    // Shared Preferences
    const val PREF_NAME = "MediaSocialPrefs"
    const val PREF_USER_ID = "userId"
    const val PREF_IS_LOGGED_IN = "isLoggedIn"
    
    // Request Codes
    const val REQUEST_IMAGE_PICK = 1001
    const val REQUEST_CAMERA = 1002
    const val REQUEST_STORAGE_PERMISSION = 1003
    
    // Intent Extras
    const val EXTRA_USER_ID = "userId"
    const val EXTRA_POST_ID = "postId"
    const val EXTRA_STORY_ID = "storyId"
    const val EXTRA_STORY_LIST = "storyList"
    const val EXTRA_STORY_INDEX = "storyIndex"
    
    // Story Duration
    const val STORY_DURATION_MS = 5000L // 5 detik per story
    const val STORY_EXPIRY_HOURS = 24
}
