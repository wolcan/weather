package com.wolcan.weather.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val query: String = ""
)