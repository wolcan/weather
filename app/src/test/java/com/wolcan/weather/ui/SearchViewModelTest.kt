package com.wolcan.weather.ui

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.wolcan.weather.TestDispatchers
import com.wolcan.weather.data.models.LocationWeather
import com.wolcan.weather.data.repositories.WeatherRepository
import com.wolcan.weather.presentation.ui.SearchUiState
import com.wolcan.weather.presentation.ui.SearchViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SearchViewModelTest {
    private lateinit var dispatchers: TestDispatchers
    private lateinit var repository: WeatherRepository
    private lateinit var viewModel: SearchViewModel
    private val fakeLocationWeatherFlow = MutableSharedFlow<LocationWeather>(replay = 1)

    @Before
    fun setup() {
        dispatchers = TestDispatchers()
        repository = mockk(relaxUnitFun = true) {
            every { getWeatherByCity(any()) } returns fakeLocationWeatherFlow

        }
        viewModel = SearchViewModel(dispatchers, repository)
    }

    @Test
    fun `uiState, is initiated correctly`() = runTest {
        val expectedState = SearchUiState(
            query = "",
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
    fun `show recently searched weather`() = runTest {
        /** Given the database has data **/
        val locationWeather = mockk<LocationWeather>()
        val recentSearchQuery = "ABC"
        fakeLocationWeatherFlow.emit(locationWeather)

        /** When calling getRecentSearch(), returns cached data **/
        coEvery { repository.getRecentSearchQuery() } returns recentSearchQuery
        coEvery { repository.getWeatherByCity(recentSearchQuery) } returns fakeLocationWeatherFlow
        viewModel.getRecentSearch()

        /** Then the UiState should show an the cached data **/
        val expectedState = SearchUiState(
            query = "",
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
    fun `show success case when searching for a city`() = runTest {
        val query = "XYZ"
        val locationWeather = mockk<LocationWeather>()

        /** When searching for a city, we get a successful response **/
        coEvery { repository.fetchWeatherByCity(query) } returns Result.success(locationWeather)
        viewModel.searchByCity(query)

        /** Then the UiState should show the success state with data **/
        val expectedState = SearchUiState(
            query = query,
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
    fun `show failed case when searching for a city`() = runTest {
        val query = "XYZ"

        /** When searching for a city, we get a successful response **/
        coEvery { repository.fetchWeatherByCity(query) } returns Result.success(null)
        viewModel.searchByCity(query)

        /** Then the UiState should show the success state with data **/
        val expectedState = SearchUiState(
            query = query,
            locationWeather = null,
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
        /** Given the database has data **/
        val query = "XYZ"
        val locationWeather = mockk<LocationWeather>()
        fakeLocationWeatherFlow.emit(locationWeather)

        /** When offline, getWeatherByCity() will throw an Error **/
        coEvery { repository.fetchWeatherByCity(any()) } returns Result.failure(Throwable())
        viewModel.searchByCity(query)

        /** Then the UiState should show an Error state **/
        val expectedState = SearchUiState(
            query = "",
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