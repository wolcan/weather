package com.wolcan.weather.data.local.entities

import androidx.room.Entity
import com.wolcan.weather.data.models.Weather

@Entity(primaryKeys = ["weatherId", "locationId"])
data class WeatherEntity(
    val weatherId: Int,
    val locationId: Int,
    val main: String,
    val description: String,
    val icon: String
)

fun WeatherEntity.asExtModel() = Weather(
    id = weatherId,
    main = main,
    description = description,
    icon = icon
)