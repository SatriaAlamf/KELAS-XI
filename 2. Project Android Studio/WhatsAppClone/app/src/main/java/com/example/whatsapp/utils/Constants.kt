package com.example.whatsapp.utils

object Constants {
    // Firebase Collections
    const val USERS_COLLECTION = "users"
    const val CONVERSATIONS_COLLECTION = "conversations"
    const val MESSAGES_COLLECTION = "messages" 
    const val STATUS_COLLECTION = "status"
    const val COMMUNITIES_COLLECTION = "communities"
    const val CALLS_COLLECTION = "calls"
    
    // Realtime Database paths
    const val PRESENCE_PATH = "presence"
    const val TYPING_PATH = "typing"
    const val SIGNALING_PATH = "signaling"
    
    // Supabase Storage buckets
    const val AVATARS_BUCKET = "avatars"
    const val MESSAGES_BUCKET = "messages"
    const val STATUS_BUCKET = "status"
    
    // Notification channels
    const val MESSAGE_CHANNEL_ID = "message_notifications"
    const val CALL_CHANNEL_ID = "call_notifications"
    
    // Request codes
    const val REQUEST_CODE_CAMERA = 1001
    const val REQUEST_CODE_GALLERY = 1002
    const val REQUEST_CODE_DOCUMENT = 1003
    const val REQUEST_CODE_LOCATION = 1004
    const val REQUEST_CODE_AUDIO_RECORD = 1005
    
    // Shared preferences
    const val PREFS_NAME = "whatsapp_prefs"
    const val PREF_USER_ID = "user_id"
    const val PREF_USER_NAME = "user_name"
    const val PREF_FCM_TOKEN = "fcm_token"
    
    // WebRTC
    const val LOCAL_TRACK_ID = "local_track"
    const val LOCAL_STREAM_ID = "local_stream"
    
    // Message limits
    const val MESSAGE_LOAD_LIMIT = 50
    const val STATUS_EXPIRY_HOURS = 24
    
    // File size limits (in bytes)
    const val MAX_IMAGE_SIZE = 16 * 1024 * 1024 // 16MB
    const val MAX_VIDEO_SIZE = 100 * 1024 * 1024 // 100MB
    const val MAX_DOCUMENT_SIZE = 100 * 1024 * 1024 // 100MB
    const val MAX_AUDIO_DURATION = 300000 // 5 minutes in milliseconds
}