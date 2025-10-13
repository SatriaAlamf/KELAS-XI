package com.example.weather.utils

object Constants {
    
    // IMPORTANT: Ganti dengan API key Anda sendiri dari OpenWeatherMap
    // Daftar gratis di: https://openweathermap.org/api
    const val API_KEY = "YOUR_API_KEY_HERE"
    
    // Base URL untuk icon cuaca
    const val ICON_BASE_URL = "https://openweathermap.org/img/wn/"
    
    // Request codes
    const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    
    // Weather condition codes
    const val THUNDERSTORM_MIN = 200
    const val THUNDERSTORM_MAX = 232
    const val DRIZZLE_MIN = 300
    const val DRIZZLE_MAX = 321
    const val RAIN_MIN = 500
    const val RAIN_MAX = 531
    const val SNOW_MIN = 600
    const val SNOW_MAX = 622
    const val ATMOSPHERE_MIN = 701
    const val ATMOSPHERE_MAX = 781
    const val CLEAR = 800
    const val CLOUDS_MIN = 801
    const val CLOUDS_MAX = 804
}
