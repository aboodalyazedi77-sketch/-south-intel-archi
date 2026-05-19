package com.southintel.archive.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.southintel.archive.data.local.entity.RecordEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAdd: () -> Unit,
    onOpen: (String) -> Unit,
    onSearch: () -> Unit,
    onSettings: () -> Unit,
    vm: HomeViewModel = hiltViewModel(),
) {
    val records by vm.records.collectAsState()
    val count by vm.count.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("أرشيف الجنوب", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = onSearch) { Icon(Icons.Default.Search, null) }
                    IconButton(onClick = { vm.syncNow() }) { Icon(Icons.Default.Sync, null) }
                    IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onAdd, icon = { Icon(Icons.Default.Add, null) }, text = { Text("إضافة سجل") })
        }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().padding(16.dp)) {
            StatsCard(count = count, latest = records.firstOrNull()?.updatedAt)
            Spacer(Modifier.height(16.dp))
            Text("السجلات الأخيرة", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            if (records.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد سجلات بعد. اضغط (إضافة سجل).",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(records, key = { it.id }) { r ->
                        RecordRow(r) { onOpen(r.id) }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsCard(count: Int, latest: Long?) {
    val fmt = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("ar")) }
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("إجمالي السجلات", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$count", style = MaterialTheme.typography.titleLarge)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("آخر تحديث", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(latest?.let { fmt.format(Date(it)) } ?: "—",
                    style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun RecordRow(r: RecordEntity, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(r.name.firstOrNull()?.toString() ?: "؟",
                    style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(r.name.ifBlank { "(بدون اسم)" }, style = MaterialTheme.typography.titleMedium)
                Text(
                    listOf(r.kunya, r.governorate, r.affiliation).filter { it.isNotBlank() }.joinToString(" • "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
