package com.wolcan.weather.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.wolcan.weather.data.models.LocationWeather

data class LocationWeatherResource(
    @Embedded val location: LocationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "locationId"
    )
    val weatherList: List<WeatherEntity>
)

fun LocationWeatherResource.asExtModel() = LocationWeather(
    id = location.id,
    name = location.name,
    lat = location.lat,
    lon = location.lon,
    temp = location.temp,
    feelsLike = location.feelsLike,
    tempMin = location.tempMin,
    tempMax = location.tempMax,
    pressure = location.pressure,
    humidity = location.humidity,
    weather = weatherList.map(WeatherEntity::asExtModel)
)
