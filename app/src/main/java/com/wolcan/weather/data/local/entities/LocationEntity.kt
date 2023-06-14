package com.wolcan.weather.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wolcan.weather.data.models.Location

@Entity
data class LocationEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val lat: Float,
    val lon: Float,
    val temp: Float,
    val feelsLike: Float,
    val tempMin: Float,
    val tempMax: Float,
    val pressure: Int,
    val humidity: Int
)

fun LocationEntity.asExtModel() = Location(
    id = id,
    name = name,
    lat = lat,
    lon = lon,
    temp = temp,
    feelsLike = feelsLike,
    tempMin = tempMin,
    tempMax = tempMax,
    pressure = pressure,
    humidity = humidity
)