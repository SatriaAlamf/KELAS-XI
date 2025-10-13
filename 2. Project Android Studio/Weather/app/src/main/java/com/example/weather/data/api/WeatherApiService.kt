package com.example.weather.data.api

import com.example.weather.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    
    /**
     * Get current weather by city name
     * @param cityName Name of the city
     * @param apiKey OpenWeatherMap API key
     * @param units Unit of measurement (metric for Celsius)
     * @param lang Language for the output
     */
    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "id"
    ): Response<WeatherResponse>
    
    /**
     * Get current weather by coordinates
     * @param lat Latitude
     * @param lon Longitude
     * @param apiKey OpenWeatherMap API key
     * @param units Unit of measurement (metric for Celsius)
     * @param lang Language for the output
     */
    @GET("weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "id"
    ): Response<WeatherResponse>
}
