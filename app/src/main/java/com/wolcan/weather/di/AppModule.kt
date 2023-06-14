package com.wolcan.weather.di

import android.app.Application
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.wolcan.weather.data.local.AppDatabase
import com.wolcan.weather.data.local.DATABASE_NAME
import com.wolcan.weather.data.remote.ApiServices
import com.wolcan.weather.data.remote.BASE_URL
import com.wolcan.weather.data.repositories.WeatherRepository
import com.wolcan.weather.presentation.ui.SearchViewModel
import com.wolcan.weather.presentation.ui.YourLocViewModel
import com.wolcan.weather.utils.DefaultDispatchers
import com.wolcan.weather.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideDispatcher(): DispatcherProvider = DefaultDispatchers()

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME)
            .build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi, client: OkHttpClient): ApiServices =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ApiServices::class.java)

    @Provides
    @Singleton
    fun provideSearchViewModel(dispatcher: DispatcherProvider, repository: WeatherRepository) =
        SearchViewModel(dispatcher, repository)

    @Provides
    @Singleton
    fun provideYourLocViewModel(dispatcher: DispatcherProvider, repository: WeatherRepository) =
        YourLocViewModel(dispatcher, repository)
}