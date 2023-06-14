@file:OptIn(
    ExperimentalGlideComposeApi::class, ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)

package com.wolcan.weather.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.wolcan.weather.R
import com.wolcan.weather.data.models.Weather
import com.wolcan.weather.presentation.ui.theme.WeatherTheme

@Composable
internal fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier.fillMaxSize()) {
        SearchBar(uiState, onSearch = {
            viewModel.searchByCity(it)
        })
        if (uiState.isError) {
            ErrorOccurred(uiState)
        } else {
            DetailView(uiState)
        }
    }
}

@Composable
private fun SearchBar(
    uiState: SearchUiState,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit
) {
    var text by remember { mutableStateOf(uiState.query) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val localFocusManager = LocalFocusManager.current
    Row(modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = text,
            maxLines = 1,
            onValueChange = { text = it },
            label = { Text(stringResource(id = R.string.search_label)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(onSearch = {
                keyboardController?.hide()
                localFocusManager.clearFocus()
                onSearch(text)
            }),
            modifier = modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(4.dp)
        )

        Image(painter = painterResource(id = R.drawable.ic_search),
            colorFilter = ColorFilter.tint(Color.Gray),
            contentDescription = "Search Button",
            modifier = modifier
                .clickable { onSearch(text) }
                .align(Alignment.CenterVertically)
                .alpha(if (uiState.isLoading) 0f else 1f)
                .size(48.dp, 48.dp)
                .padding(8.dp)
        )

        Spacer(modifier = modifier.size(8.dp))
    }
}

@Composable
private fun DetailView(uiState: SearchUiState, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(32.dp)
            )
        } else if (uiState.locationWeather != null) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Text(
                    text = stringResource(
                        id = R.string.search_result_city_us,
                        uiState.locationWeather?.name ?: ""
                    ),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    LocationBlock(uiState, modifier.weight(0.5f))
                    Spacer(modifier = modifier.size(8.dp))
                    TempBlock(uiState, modifier.weight(0.5f))
                }
                WeatherBlock(uiState, modifier)

            }
        } else {
            Column(modifier.fillMaxSize()) {
                Text(
                    text = stringResource(id = R.string.search_result_no_city_found),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 32.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_location_not_found),
                    contentDescription = stringResource(id = R.string.search_result_no_city_found),
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(32.dp)
                )
            }
        }
    }
}

@Composable
private fun LocationBlock(uiState: SearchUiState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFCEA37D), shape = RoundedCornerShape(15.dp))
            .padding(8.dp)
    ) {
        Text(
            text = stringResource(
                id = R.string.search_result_label_location_id,
                uiState.locationWeather?.id.toString()
            )
        )
        Text(
            text = stringResource(
                id = R.string.search_result_label_coord,
                "${uiState.locationWeather?.lat}, ${uiState.locationWeather?.lon}"
            )
        )
    }
}

@Composable
private fun WeatherBlock(uiState: SearchUiState, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = R.string.search_result_label_weather),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = modifier.size(8.dp))
    LazyRow(modifier = modifier.fillMaxWidth()) {
        uiState.locationWeather?.weather?.let { weatherList ->
            items(weatherList.size) {
                for (weather in weatherList) {
                    WeatherItem(weather)
                    Spacer(modifier = modifier.size(8.dp))
                }
            }
        }
    }
}

@Composable
private fun WeatherItem(weather: Weather, modifier: Modifier = Modifier) {
    Column(
        modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF7DCEA0), shape = RoundedCornerShape(15.dp))
            .padding(8.dp)
    ) {
        GlideImage(
            model = "https://openweathermap.org/img/wn/${weather.icon}@2x.png",
            contentDescription = "Weather Icon",
            modifier = modifier
                .align(Alignment.CenterHorizontally)
                .size(96.dp)
        )
        Text(
            text = weather.description,
            modifier
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun TempBlock(uiState: SearchUiState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF7DCBCE), shape = RoundedCornerShape(15.dp))
            .padding(8.dp)
    ) {
        Text(text = "Temp: ${uiState.locationWeather?.temp} °F")
        Text(text = "Min Temp: ${uiState.locationWeather?.tempMin} °F")
        Text(text = "Max Temp:${uiState.locationWeather?.tempMax} °F")
        Text(text = "Feels Like: ${uiState.locationWeather?.feelsLike} °F")
        Text(text = "Pressure: ${uiState.locationWeather?.pressure} hPa")
        Text(text = "Humidity: ${uiState.locationWeather?.humidity}%")
    }
}

@Composable
fun ErrorOccurred(uiState: SearchUiState, modifier: Modifier = Modifier) {
    if (uiState.isError) {
        Column(
            modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_no_internet),
                contentDescription = stringResource(id = R.string.search_result_no_city_found),
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
            Text(
                text = stringResource(id = R.string.error_occurred),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    WeatherTheme {
        val uiState = SearchUiState().apply {
            query = "Austin"
            isLoading = true
        }
        SearchBar(uiState) {}
    }
}