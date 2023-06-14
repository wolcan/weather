package com.wolcan.weather.presentation.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolcan.weather.data.models.LocationWeather
import com.wolcan.weather.data.repositories.WeatherRepository
import com.wolcan.weather.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    var query: String = "",
    var locationWeather: LocationWeather? = null,
    var isLoading: Boolean = true,
    var isError: Boolean = false,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val dispatcher: DispatcherProvider,
    private val repository: WeatherRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        getRecentSearch()
    }

    fun getRecentSearch() {
        viewModelScope.launch(dispatcher.io) {
            val recentQuery = repository.getRecentSearchQuery()
            recentQuery?.let { query ->
                val locationWeather = repository.getWeatherByCity(query).first()
                _uiState.update {
                    it.copy(
                        locationWeather = locationWeather,
                        isLoading = false,
                        isError = false
                    )
                }
            }
        }
    }

    fun searchByCity(query: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch(dispatcher.io) {
            val response = repository.fetchWeatherByCity(query)
            if (response.isSuccess) {
                _uiState.update {
                    it.copy(
                        query = query,
                        locationWeather = response.getOrNull(),
                        isLoading = false,
                        isError = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        locationWeather = null,
                        isLoading = false,
                        isError = true
                    )
                }
            }
        }
    }
}