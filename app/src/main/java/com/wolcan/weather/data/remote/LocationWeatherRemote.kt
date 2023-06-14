package com.wolcan.weather.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wolcan.weather.data.local.entities.LocationEntity
import com.wolcan.weather.data.local.entities.LocationWeatherResource
import com.wolcan.weather.data.local.entities.WeatherEntity

@JsonClass(generateAdapter = true)
data class LocationWeatherRemote(
    val id: Int,
    val name: String,
    val coord: Coord,
    val weather: List<Weather>,
    val main: Temp,
)

@JsonClass(generateAdapter = true)
class Coord(
    val lon: Float,
    val lat: Float
)

@JsonClass(generateAdapter = true)
class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@JsonClass(generateAdapter = true)
class Temp(
    val temp: Float,
    @Json(name = "feels_like")
    val feelsLike: Float,
    @Json(name = "temp_min")
    val tempMin: Float,
    @Json(name = "temp_max")
    val tempMax: Float,
    val pressure: Int,
    val humidity: Int
)

fun LocationWeatherRemote.asEntity() = LocationWeatherResource(
    location = LocationEntity(
        id = id,
        name = name,
        lat = coord.lat,
        lon = coord.lon,
        temp = main.temp,
        feelsLike = main.feelsLike,
        tempMin = main.tempMin,
        tempMax = main.tempMax,
        pressure = main.pressure,
        humidity = main.humidity
    ),
    weatherList = weather.map {
        WeatherEntity(
            weatherId = it.id,
            locationId = id,
            main = it.main,
            description = it.description,
            icon = it.icon
        )
    }
)
