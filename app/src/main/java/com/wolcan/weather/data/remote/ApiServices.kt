package com.wolcan.weather.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
const val API_KEY = "c8309ef039d5af34af8f9873e0191304"

interface ApiServices {
    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") query: String,
        @Query("units") units: String = "imperial",
        @Query("appId") apiKey: String = API_KEY,
    ): Response<LocationWeatherRemote>

    @GET("weather")
    suspend fun getWeatherByLatLon(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String = "imperial",
        @Query("appId") apiKey: String = API_KEY,
    ): Response<LocationWeatherRemote>
}