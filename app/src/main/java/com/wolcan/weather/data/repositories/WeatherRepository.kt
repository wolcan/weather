package com.wolcan.weather.data.repositories

import com.wolcan.weather.data.models.LocationWeather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun getRecentSearchQuery(): String?

    fun getWeatherByCity(query: String): Flow<LocationWeather>

    suspend fun fetchWeatherByCity(query: String): Result<LocationWeather?>

    suspend fun fetchWeatherByLatLon(lat: String, lon: String): Result<LocationWeather>
}