package com.wolcan.weather.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wolcan.weather.data.local.entities.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM WeatherEntity")
    fun getWeather(): Flow<List<WeatherEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weather: WeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(weather: List<WeatherEntity>)

    @Delete
    suspend fun delete(weather: WeatherEntity)

}