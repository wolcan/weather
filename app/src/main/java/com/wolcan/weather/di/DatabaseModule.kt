package com.wolcan.weather.di

import com.wolcan.weather.data.local.AppDatabase
import com.wolcan.weather.data.local.dao.LocationWeatherDao
import com.wolcan.weather.data.local.dao.SearchHistoryDao
import com.wolcan.weather.data.local.dao.WeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun provideLocationWeatherDao(db: AppDatabase): LocationWeatherDao = db.locationWeatherDao()

    @Provides
    fun provideWeatherDao(db: AppDatabase): WeatherDao = db.weatherDao()

    @Provides
    fun provideSearchHistoryDao(db: AppDatabase): SearchHistoryDao = db.searchHistoryDao()
}