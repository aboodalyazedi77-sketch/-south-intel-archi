package com.southintel.archive.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.southintel.archive.ui.screens.home.RecordRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
    vm: SearchViewModel = hiltViewModel(),
) {
    val f by vm.filters.collectAsState()
    val results by vm.results.collectAsState()
    val govs by vm.governorates.collectAsState()
    val affs by vm.affiliations.collectAsState()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("بحث وفلاتر") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                }
            }
        )
    }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp).fillMaxSize()) {
            OutlinedTextField(
                value = f.q,
                onValueChange = { v -> vm.update { it.copy(q = v) } },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                placeholder = { Text("ابحث في كل الحقول…") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            DropdownFilter("المحافظة", f.governorate, govs) { v -> vm.update { it.copy(governorate = v) } }
            Spacer(Modifier.height(8.dp))
            DropdownFilter("الانتماء", f.affiliation, affs) { v -> vm.update { it.copy(affiliation = v) } }
            Spacer(Modifier.height(12.dp))
            Text("النتائج: ${results.size}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(results, key = { it.id }) { r -> RecordRow(r) { onOpen(r.id) } }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownFilter(label: String, value: String, options: List<String>, onChange: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = open, onExpandedChange = { open = it }) {
        OutlinedTextField(
            value = if (value.isBlank()) "الكل" else value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = open) }
        )
        ExposedDropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            DropdownMenuItem(text = { Text("الكل") }, onClick = { onChange(""); open = false })
            options.forEach { opt ->
                DropdownMenuItem(text = { Text(opt) }, onClick = { onChange(opt); open = false })
            }
        }
    }
}
