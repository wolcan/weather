package com.wolcan.weather.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.wolcan.weather.data.local.entities.SearchHistoryEntity

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM SearchHistoryEntity ORDER BY id DESC LIMIT 1 ")
    fun getLatest(): SearchHistoryEntity?

    @Insert
    suspend fun insert(query: SearchHistoryEntity)

    @Delete
    suspend fun delete(locationWeather: SearchHistoryEntity)
}