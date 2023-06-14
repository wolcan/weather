package com.wolcan.weather.ui

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.wolcan.weather.TestDispatchers
import com.wolcan.weather.data.models.LocationWeather
import com.wolcan.weather.data.repositories.WeatherRepository
import com.wolcan.weather.presentation.ui.SearchUiState
import com.wolcan.weather.presentation.ui.YourLocUiState
import com.wolcan.weather.presentation.ui.YourLocViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class YourLocViewModelTest {
    private lateinit var dispatchers: TestDispatchers
    private lateinit var repository: WeatherRepository
    private lateinit var viewModel: YourLocViewModel
    private val fakeLocationWeatherFlow = MutableSharedFlow<LocationWeather>(replay = 1)

    @Before
    fun setup() {
        dispatchers = TestDispatchers()
        repository = mockk(relaxUnitFun = true) {
            every { getWeatherByCity(any()) } returns fakeLocationWeatherFlow
        }
        viewModel = YourLocViewModel(dispatchers, repository)
    }

    @Test
    fun `uiState, is initiated correctly`() = runTest {
        val expectedState = YourLocUiState(
            locationWeather = null,
            isLoading = true,
            isError = false,
        )

        val job = launch {
            viewModel.uiState.test {
                val emission = awaitItem()
                assertThat(emission).isEqualTo(expectedState)
                cancelAndConsumeRemainingEvents()
            }
        }
        job.join()
        job.cancel()
    }

    @Test
    fun `show success case for your location`() = runTest {
        val lat = "0.0"
        val lon = "0.0"
        val locationWeather = mockk<LocationWeather>()

        /** When searching for a lat/lon, we get a successful response **/
        coEvery { repository.fetchWeatherByLatLon(any(), any()) } returns Result.success(
            locationWeather
        )
        viewModel.fetchWeatherByLatLon(lat, lon)

        /** Then the UiState should show the success state with data **/
        val expectedState = YourLocUiState(
            locationWeather = locationWeather,
            isLoading = false,
            isError = false,
        )

        val job = launch {
            viewModel.uiState.test {
                val emission = awaitItem()
                assertThat(emission).isEqualTo(expectedState)
                cancelAndConsumeRemainingEvents()
            }
        }
        job.join()
        job.cancel()
    }

    @Test
    fun `show error when offline`() = runTest {
        val lat = "0.0"
        val lon = "0.0"
        val locationWeather = mockk<LocationWeather>()

        /** When searching for a lat/lon, we get a successful response **/
        coEvery {
            repository.fetchWeatherByLatLon(
                any(), any()
            )
        } returns Result.failure(Throwable())

        viewModel.fetchWeatherByLatLon(lat, lon)

        /** Then the UiState should show the success state with data **/
        val expectedState = YourLocUiState(
            locationWeather = null,
            isLoading = false,
            isError = true,
        )

        val job = launch {
            viewModel.uiState.test {
                val emission = awaitItem()
                assertThat(emission).isEqualTo(expectedState)
                cancelAndConsumeRemainingEvents()
            }
        }
        job.join()
        job.cancel()
    }
}