package com.example.weather.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.data.model.WeatherResponse
import com.example.weather.ui.viewmodel.WeatherViewModel
import com.example.weather.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // UI Components
    private lateinit var mainContainer: RelativeLayout
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var locationButton: ImageButton
    private lateinit var cityNameText: TextView
    private lateinit var dateTimeText: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var temperatureText: TextView
    private lateinit var weatherDescriptionText: TextView
    private lateinit var feelsLikeText: TextView
    private lateinit var humidityText: TextView
    private lateinit var windSpeedText: TextView
    private lateinit var pressureText: TextView
    private lateinit var loadingProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupViewModel()
        setupLocationClient()
        setupClickListeners()
        observeViewModel()

        // Load weather for current location if permission granted
        if (hasLocationPermission()) {
            getCurrentLocationWeather()
        }
    }

    private fun initializeViews() {
        mainContainer = findViewById(R.id.main_container)
        searchEditText = findViewById(R.id.search_edit_text)
        searchButton = findViewById(R.id.search_button)
        locationButton = findViewById(R.id.location_button)
        cityNameText = findViewById(R.id.city_name_text)
        dateTimeText = findViewById(R.id.date_time_text)
        weatherIcon = findViewById(R.id.weather_icon)
        temperatureText = findViewById(R.id.temperature_text)
        weatherDescriptionText = findViewById(R.id.weather_description_text)
        feelsLikeText = findViewById(R.id.feels_like_text)
        humidityText = findViewById(R.id.humidity_text)
        windSpeedText = findViewById(R.id.wind_speed_text)
        pressureText = findViewById(R.id.pressure_text)
        loadingProgress = findViewById(R.id.loading_progress)

        // Set current date and time
        updateDateTime()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
    }

    private fun setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupClickListeners() {
        searchButton.setOnClickListener {
            val cityName = searchEditText.text.toString().trim()
            if (cityName.isNotEmpty()) {
                viewModel.fetchWeatherByCity(cityName)
                hideKeyboard()
            } else {
                Toast.makeText(this, "Masukkan nama kota", Toast.LENGTH_SHORT).show()
            }
        }

        locationButton.setOnClickListener {
            if (hasLocationPermission()) {
                getCurrentLocationWeather()
            } else {
                Toast.makeText(
                    this,
                    "Izin lokasi tidak diberikan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Handle keyboard search action
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchButton.performClick()
                true
            } else {
                false
            }
        }
    }

    private fun observeViewModel() {
        viewModel.weatherData.observe(this) { weatherResponse ->
            weatherResponse?.let {
                updateUI(it)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            loadingProgress.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocationWeather() {
        if (!hasLocationPermission()) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                viewModel.fetchWeatherByCoordinates(location.latitude, location.longitude)
            } else {
                Toast.makeText(
                    this,
                    "Tidak dapat mendapatkan lokasi saat ini",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.addOnFailureListener {
            Toast.makeText(
                this,
                "Gagal mendapatkan lokasi: ${it.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateUI(weather: WeatherResponse) {
        // City name
        cityNameText.text = weather.name ?: "Unknown"

        // Temperature
        val temp = weather.main?.temp?.roundToInt() ?: 0
        temperatureText.text = "${temp}°"

        // Weather description
        val description = weather.weather?.firstOrNull()?.description ?: ""
        weatherDescriptionText.text = description.uppercase()

        // Feels like
        val feelsLike = weather.main?.feelsLike?.roundToInt() ?: 0
        feelsLikeText.text = "${feelsLike}°C"

        // Humidity
        val humidity = weather.main?.humidity ?: 0
        humidityText.text = "$humidity%"

        // Wind speed (convert m/s to km/h)
        val windSpeed = ((weather.wind?.speed ?: 0.0) * 3.6).roundToInt()
        windSpeedText.text = "$windSpeed km/h"

        // Pressure
        val pressure = weather.main?.pressure ?: 0
        pressureText.text = "$pressure hPa"

        // Weather icon
        val iconCode = weather.weather?.firstOrNull()?.icon
        if (iconCode != null) {
            val iconUrl = "${Constants.ICON_BASE_URL}${iconCode}@4x.png"
            Glide.with(this)
                .load(iconUrl)
                .into(weatherIcon)
        }

        // Update background based on weather condition
        updateBackgroundBasedOnWeather(weather.weather?.firstOrNull()?.id ?: 800)

        // Clear search text
        searchEditText.text.clear()
    }

    private fun updateBackgroundBasedOnWeather(weatherCode: Int) {
        val backgroundRes = when {
            weatherCode in Constants.THUNDERSTORM_MIN..Constants.THUNDERSTORM_MAX -> R.drawable.bg_thunderstorm
            weatherCode in Constants.DRIZZLE_MIN..Constants.DRIZZLE_MAX -> R.drawable.bg_rain
            weatherCode in Constants.RAIN_MIN..Constants.RAIN_MAX -> R.drawable.bg_rain
            weatherCode in Constants.SNOW_MIN..Constants.SNOW_MAX -> R.drawable.bg_snow
            weatherCode in Constants.ATMOSPHERE_MIN..Constants.ATMOSPHERE_MAX -> R.drawable.bg_mist
            weatherCode == Constants.CLEAR -> R.drawable.bg_clear_day
            weatherCode in Constants.CLOUDS_MIN..Constants.CLOUDS_MAX -> R.drawable.bg_clouds
            else -> R.drawable.bg_clear_day
        }
        
        mainContainer.setBackgroundResource(backgroundRes)
    }

    private fun updateDateTime() {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy | HH:mm", Locale("id", "ID"))
        dateTimeText.text = dateFormat.format(Date())
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }
}