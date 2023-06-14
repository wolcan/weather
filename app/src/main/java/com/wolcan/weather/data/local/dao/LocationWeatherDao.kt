package com.wolcan.weather.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.wolcan.weather.data.local.entities.LocationEntity
import com.wolcan.weather.data.local.entities.LocationWeatherResource
import com.wolcan.weather.data.models.LocationWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationWeatherDao {

    @Transaction
    @Query("SELECT * FROM LocationEntity WHERE name=:query LIMIT 1")
    fun getByCity(query: String): Flow<LocationWeatherResource?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(locationWeather: LocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(locationWeather: List<LocationEntity>)

    @Delete
    suspend fun delete(locationWeather: LocationEntity)

}