package com.example.weather.data.repository

import com.example.weather.data.api.RetrofitInstance
import com.example.weather.data.model.WeatherResponse
import com.example.weather.utils.Constants
import retrofit2.Response

class WeatherRepository {
    
    private val api = RetrofitInstance.api
    
    /**
     * Fetch weather data by city name
     */
    suspend fun getWeatherByCity(cityName: String): Response<WeatherResponse> {
        return api.getWeatherByCity(
            cityName = cityName,
            apiKey = Constants.API_KEY
        )
    }
    
    /**
     * Fetch weather data by coordinates (latitude and longitude)
     */
    suspend fun getWeatherByCoordinates(lat: Double, lon: Double): Response<WeatherResponse> {
        return api.getWeatherByCoordinates(
            lat = lat,
            lon = lon,
            apiKey = Constants.API_KEY
        )
    }
}
