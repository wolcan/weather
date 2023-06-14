package com.wolcan.weather.data.repositories

import com.wolcan.weather.data.local.dao.LocationWeatherDao
import com.wolcan.weather.data.local.dao.SearchHistoryDao
import com.wolcan.weather.data.local.dao.WeatherDao
import com.wolcan.weather.data.local.entities.SearchHistoryEntity
import com.wolcan.weather.data.local.entities.asExtModel
import com.wolcan.weather.data.models.LocationWeather
import com.wolcan.weather.data.remote.ApiServices
import com.wolcan.weather.data.remote.asEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao,
    private val locationDao: LocationWeatherDao,
    private val weatherDao: WeatherDao,
    private val api: ApiServices
) : WeatherRepository {

    override suspend fun getRecentSearchQuery(): String? {
        return searchHistoryDao.getLatest()?.query
    }

    override fun getWeatherByCity(query: String): Flow<LocationWeather> {
        return locationDao.getByCity(query).map {
            it?.asExtModel() ?: LocationWeather()
        }
    }

    override suspend fun fetchWeatherByCity(query: String): Result<LocationWeather?> {
        try {
            val response = api.getWeatherByCity("$query, US")
            if (response.isSuccessful) {
                response.body()?.let {
                    val locationWeatherEntity = it.asEntity()
                    searchHistoryDao.insert(SearchHistoryEntity(query = locationWeatherEntity.location.name))
                    locationDao.insert(locationWeatherEntity.location)
                    weatherDao.insertAll(locationWeatherEntity.weatherList)
                    val locationWeather = locationWeatherEntity.asExtModel()
                    return Result.success(locationWeather)
                } ?: return Result.success(null)
            } else if (response.code() == 404) {
                return Result.success(null)
            } else return Result.failure(Throwable())
        } catch (e: HttpException) {
            return Result.failure(e)
        } catch (e: IOException) {
            return Result.failure(e)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun fetchWeatherByLatLon(lat: String, lon: String): Result<LocationWeather> {
        try {
            val response = api.getWeatherByLatLon(lat = lat, lon = lon)
            if (response.isSuccessful) {
                response.body()?.let {
                    val locationWeatherEntity = it.asEntity()
                    locationDao.insert(locationWeatherEntity.location)
                    weatherDao.insertAll(locationWeatherEntity.weatherList)
                    val locationWeather = locationWeatherEntity.asExtModel()
                    return Result.success(locationWeather)
                } ?: return Result.success(LocationWeather())
            } else {
                return Result.failure(Throwable())
            }
        } catch (e: HttpException) {
            return Result.failure(e)
        } catch (e: IOException) {
            return Result.failure(e)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}