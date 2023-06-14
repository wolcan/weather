package com.wolcan.weather.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolcan.weather.data.models.LocationWeather
import com.wolcan.weather.data.repositories.WeatherRepository
import com.wolcan.weather.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class YourLocUiState(
    var locationWeather: LocationWeather? = null,
    var isLoading: Boolean = true,
    var isError: Boolean = false,
)

@HiltViewModel
class YourLocViewModel @Inject constructor(
    private val dispatcher: DispatcherProvider,
    private val repository: WeatherRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(YourLocUiState())
    val uiState: StateFlow<YourLocUiState> = _uiState.asStateFlow()

    fun fetchWeatherByLatLon(lat: String, lon: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch(dispatcher.io) {
            val response = repository.fetchWeatherByLatLon(lat, lon)
            if (response.isSuccess) {
                _uiState.update {
                    it.copy(
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