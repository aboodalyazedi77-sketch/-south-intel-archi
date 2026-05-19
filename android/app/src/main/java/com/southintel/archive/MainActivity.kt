package com.southintel.archive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import com.southintel.archive.ui.navigation.AppNav
import com.southintel.archive.ui.screens.settings.SettingsViewModel
import com.southintel.archive.ui.theme.ArchiveTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsVm: SettingsViewModel = hiltViewModel()
            val dark by settingsVm.darkMode.collectAsState(initial = true)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                ArchiveTheme(darkTheme = dark) {
                    AppNav()
                }
            }
        }
    }
}
