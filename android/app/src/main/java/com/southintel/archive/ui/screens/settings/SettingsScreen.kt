package com.southintel.archive.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLoggedOut: () -> Unit,
    vm: SettingsViewModel = hiltViewModel(),
) {
    val dark by vm.darkMode.collectAsState(initial = true)
    val cloud by vm.cloudSync.collectAsState(initial = true)
    val email by vm.email.collectAsState(initial = null)
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { vm.messages.collect { snackbar.showSnackbar(it) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الإعدادات") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp).fillMaxSize()) {
            email?.let {
                Card { ListItem(headlineContent = { Text("الحساب") }, supportingContent = { Text(it) }) }
                Spacer(Modifier.height(12.dp))
            }
            Card {
                Column {
                    ListItem(
                        headlineContent = { Text("الوضع الداكن") },
                        trailingContent = { Switch(checked = dark, onCheckedChange = vm::setDark) }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("المزامنة السحابية") },
                        trailingContent = { Switch(checked = cloud, onCheckedChange = vm::setCloud) }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Card { Column {
                ActionRow("المزامنة الآن") { vm.syncNow() }
                HorizontalDivider()
                ActionRow("إنشاء نسخة احتياطية") { vm.backup() }
                HorizontalDivider()
                ActionRow("استعادة من النسخة") { vm.restore() }
            } }
            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = { vm.logout(onLoggedOut) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("تسجيل الخروج", color = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
private fun ActionRow(text: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(text) },
        modifier = Modifier.then(Modifier).let { it },
        trailingContent = { TextButton(onClick = onClick) { Text("تنفيذ") } }
    )
}
