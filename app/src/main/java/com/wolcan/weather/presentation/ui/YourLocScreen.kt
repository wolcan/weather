package com.wolcan.weather.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.wolcan.weather.R
import com.wolcan.weather.data.models.Weather

lateinit var locationProvider: FusedLocationProviderClient

data class LatLon(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun YourLocScreen(
    viewModel: YourLocViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentUserLocation by remember { mutableStateOf(LatLon()) }
    locationProvider = LocationServices.getFusedLocationProviderClient(LocalContext.current)

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )
    if (locationPermissionsState.allPermissionsGranted) {
        if (uiState.isError) {
            ErrorOccurred(uiState)
        } else {
            DetailView(uiState)
        }

        if (currentUserLocation.latitude == 0.0 && currentUserLocation.longitude == 0.0) {
            locationProvider.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        val lat = location.latitude
                        val long = location.longitude
                        // Update data class with location data
                        currentUserLocation = LatLon(latitude = lat, longitude = long)
                        viewModel.fetchWeatherByLatLon(
                            currentUserLocation.latitude.toString(),
                            currentUserLocation.longitude.toString()
                        )
                    }
                }
                .addOnFailureListener {
                    Log.e("Location_error", "${it.message}")
                }
        }

    } else {
        LocPermissionRequest(locationPermissionsState)
    }
}

@Composable
private fun DetailView(uiState: YourLocUiState, modifier: Modifier = Modifier) {
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
private fun LocationBlock(uiState: YourLocUiState, modifier: Modifier = Modifier) {
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
private fun WeatherBlock(uiState: YourLocUiState, modifier: Modifier = Modifier) {
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

@OptIn(ExperimentalGlideComposeApi::class)
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
private fun TempBlock(uiState: YourLocUiState, modifier: Modifier = Modifier) {
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
fun ErrorOccurred(uiState: YourLocUiState, modifier: Modifier = Modifier) {
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocPermissionRequest(
    locationPermissionsState: MultiplePermissionsState,
    modifier: Modifier = Modifier
) {
    val allPermissionsRevoked =
        locationPermissionsState.permissions.size ==
                locationPermissionsState.revokedPermissions.size

    val buttonText = if (!allPermissionsRevoked) {
        "Allow precise location"
    } else {
        "Request permissions"
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text(text = "This feature requires location permission")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }) {
            Text(buttonText)
        }
    }
}