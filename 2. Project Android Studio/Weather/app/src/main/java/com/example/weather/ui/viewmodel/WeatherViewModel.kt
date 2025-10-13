package com.example.weather.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.data.model.WeatherResponse
import com.example.weather.data.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    
    private val repository = WeatherRepository()
    
    // LiveData for weather data
    private val _weatherData = MutableLiveData<WeatherResponse?>()
    val weatherData: LiveData<WeatherResponse?> = _weatherData
    
    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // LiveData for error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    /**
     * Fetch weather by city name
     */
    fun fetchWeatherByCity(cityName: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val response = repository.getWeatherByCity(cityName)
                
                if (response.isSuccessful && response.body() != null) {
                    _weatherData.value = response.body()
                } else {
                    _errorMessage.value = when (response.code()) {
                        404 -> "Kota tidak ditemukan"
                        401 -> "API Key tidak valid"
                        else -> "Gagal mengambil data cuaca"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message ?: "Terjadi kesalahan"}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Fetch weather by coordinates
     */
    fun fetchWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val response = repository.getWeatherByCoordinates(lat, lon)
                
                if (response.isSuccessful && response.body() != null) {
                    _weatherData.value = response.body()
                } else {
                    _errorMessage.value = when (response.code()) {
                        401 -> "API Key tidak valid"
                        else -> "Gagal mengambil data cuaca"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message ?: "Terjadi kesalahan"}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
