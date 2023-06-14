package com.wolcan.weather.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.wolcan.weather.presentation.components.MainScreenNavigationConfigurations
import com.wolcan.weather.presentation.components.WeatherBottomNavigation
import com.wolcan.weather.presentation.ui.theme.WeatherTheme
import dagger.hilt.android.AndroidEntryPoint

@OptIn(
    ExperimentalMaterial3Api::class
)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView()
                }
            }
        }
    }

    @Composable
    fun MainView(modifier: Modifier = Modifier) {
        val navController = rememberNavController()

        Scaffold(
            bottomBar = { WeatherBottomNavigation(navController) },
            content = {
                Column(
                    modifier
                        .padding(it)
                        .fillMaxSize()
                ) {
                    MainScreenNavigationConfigurations(navController = navController)
                }
            }
        )
    }
}

