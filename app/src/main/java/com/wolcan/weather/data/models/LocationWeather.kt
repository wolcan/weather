package com.wolcan.weather.data.models

data class LocationWeather(
    val id: Int = -1,
    val name: String = "",
    val lat: Float = 0f,
    val lon: Float = 0f,
    val weather: List<Weather> = listOf(),
    val temp: Float = 0f,
    val feelsLike: Float = 0f,
    val tempMin: Float = 0f,
    val tempMax: Float = 0f,
    val pressure: Int = 0,
    val humidity: Int = 0
)