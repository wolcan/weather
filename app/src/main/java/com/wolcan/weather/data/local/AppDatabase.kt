package com.wolcan.weather.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wolcan.weather.data.local.dao.LocationWeatherDao
import com.wolcan.weather.data.local.dao.SearchHistoryDao
import com.wolcan.weather.data.local.dao.WeatherDao
import com.wolcan.weather.data.local.entities.LocationEntity
import com.wolcan.weather.data.local.entities.SearchHistoryEntity
import com.wolcan.weather.data.local.entities.WeatherEntity

const val DATABASE_NAME = "weather-database"

@Database(
    entities = [LocationEntity::class, WeatherEntity::class, SearchHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationWeatherDao(): LocationWeatherDao
    abstract fun weatherDao(): WeatherDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}