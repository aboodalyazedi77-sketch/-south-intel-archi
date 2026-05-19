package com.southintel.archive.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.southintel.archive.ui.screens.auth.AuthScreen
import com.southintel.archive.ui.screens.auth.AuthViewModel
import com.southintel.archive.ui.screens.home.HomeScreen
import com.southintel.archive.ui.screens.record.RecordScreen
import com.southintel.archive.ui.screens.search.SearchScreen
import com.southintel.archive.ui.screens.settings.SettingsScreen
import com.southintel.archive.ui.screens.settings.SettingsViewModel

object Routes {
    const val AUTH = "auth"
    const val HOME = "home"
    const val SEARCH = "search"
    const val SETTINGS = "settings"
    const val RECORD = "record?id={id}"
    fun record(id: String? = null) = "record?id=${id ?: ""}"
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val settingsVm: SettingsViewModel = hiltViewModel()
    val token by settingsVm.token.collectAsState(initial = null)
    val start = if (token.isNullOrBlank()) Routes.AUTH else Routes.HOME

    NavHost(navController = nav, startDestination = start) {
        composable(Routes.AUTH) {
            val vm: AuthViewModel = hiltViewModel()
            AuthScreen(vm) { nav.navigate(Routes.HOME) { popUpTo(Routes.AUTH) { inclusive = true } } }
        }
        composable(Routes.HOME) {
            HomeScreen(
                onAdd = { nav.navigate(Routes.record()) },
                onOpen = { id -> nav.navigate(Routes.record(id)) },
                onSearch = { nav.navigate(Routes.SEARCH) },
                onSettings = { nav.navigate(Routes.SETTINGS) },
            )
        }
        composable(Routes.SEARCH) {
            SearchScreen(
                onBack = { nav.popBackStack() },
                onOpen = { id -> nav.navigate(Routes.record(id)) }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { nav.popBackStack() },
                onLoggedOut = { nav.navigate(Routes.AUTH) { popUpTo(0) } }
            )
        }
        composable(Routes.RECORD) { entry ->
            val id = entry.arguments?.getString("id")?.ifBlank { null }
            RecordScreen(recordId = id, onBack = { nav.popBackStack() })
        }
    }
}
