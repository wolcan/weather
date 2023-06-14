package com.wolcan.weather.di

import com.wolcan.weather.data.repositories.WeatherRepository
import com.wolcan.weather.data.repositories.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindWeatherRepository(
        repository: WeatherRepositoryImpl
    ): WeatherRepository
}