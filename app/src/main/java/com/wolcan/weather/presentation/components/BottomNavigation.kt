package com.wolcan.weather.presentation.components

import androidx.annotation.StringRes
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.wolcan.weather.R
import com.wolcan.weather.presentation.ui.SearchScreen
import com.wolcan.weather.presentation.ui.YourLocScreen

sealed class BottomNavigationScreens(
    val route: String,
    @StringRes val textId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object YourLocation :
        BottomNavigationScreens(
            route = "YourLocation",
            textId = R.string.nav_item_your_location,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        )

    object Search :
        BottomNavigationScreens(
            route = "Search",
            textId = R.string.nav_item_search,
            selectedIcon = Icons.Filled.Search,
            unselectedIcon = Icons.Outlined.Search
        )
}

private val items = listOf(
    BottomNavigationScreens.YourLocation,
    BottomNavigationScreens.Search
)

@Composable
fun WeatherBottomNavigation(navController: NavHostController, modifier: Modifier = Modifier) {

    val currentRoute = currentRoute(navController)

    BottomNavigation(
        backgroundColor = MaterialTheme.colors.secondaryVariant,
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        if (currentRoute == item.route) {
                            item.selectedIcon
                        } else {
                            item.unselectedIcon
                        },
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(id = item.textId)) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route)
                        navController.navigate(item.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                    inclusive = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                }
            )
        }
    }
}

@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    return navBackStackEntry.value?.destination?.route
}

@Composable
fun MainScreenNavigationConfigurations(
    navController: NavHostController
) {
    NavHost(navController, startDestination = BottomNavigationScreens.YourLocation.route) {
        composable(BottomNavigationScreens.YourLocation.route) {
            YourLocScreen()

        }
        composable(BottomNavigationScreens.Search.route) {
            SearchScreen()
        }
    }
}
