package com.southintel.archive.ui.screens.record

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    recordId: String?,
    onBack: () -> Unit,
    vm: RecordViewModel = hiltViewModel(),
) {
    LaunchedEffect(recordId) { vm.load(recordId) }
    val r by vm.state.collectAsState()
    val loaded by vm.loaded.collectAsState()
    var confirmDelete by remember { mutableStateOf(false) }

    if (!loaded || r == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val rec = r!!

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(if (recordId.isNullOrBlank()) "إضافة سجل" else "تعديل السجل") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowForward, null) } },
            actions = {
                if (!recordId.isNullOrBlank()) {
                    IconButton(onClick = { confirmDelete = true }) {
                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        )
    }, bottomBar = {
        Surface(tonalElevation = 3.dp) {
            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("إلغاء") }
                Button(onClick = { vm.save(onBack) }, modifier = Modifier.weight(1f)) { Text("حفظ") }
            }
        }
    }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState())) {
            Field("الاسم", rec.name) { v -> vm.update { it.copy(name = v) } }
            Field("الكنية", rec.kunya) { v -> vm.update { it.copy(kunya = v) } }
            Field("الجنسية", rec.nationality) { v -> vm.update { it.copy(nationality = v) } }
            Field("المحافظة", rec.governorate) { v -> vm.update { it.copy(governorate = v) } }
            Field("السكن", rec.residence) { v -> vm.update { it.copy(residence = v) } }
            Field("العمل", rec.work) { v -> vm.update { it.copy(work = v) } }
            Field("العمر", rec.age?.toString() ?: "", keyboard = KeyboardType.Number) { v ->
                vm.update { it.copy(age = v.toIntOrNull()) }
            }
            Field("المؤهل", rec.qualification) { v -> vm.update { it.copy(qualification = v) } }
            Field("الانتماء", rec.affiliation) { v -> vm.update { it.copy(affiliation = v) } }
            Field("المعلومات", rec.information, lines = 3) { v -> vm.update { it.copy(information = v) } }
            Field("الملاحظات", rec.notes, lines = 3) { v -> vm.update { it.copy(notes = v) } }

            Spacer(Modifier.height(8.dp))
            val fmt = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("ar")) }
            Text("تاريخ الإنشاء: ${fmt.format(Date(rec.createdAt))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("آخر تحديث: ${fmt.format(Date(rec.updatedAt))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("تأكيد الحذف") },
            text = { Text("هل تريد حذف هذا السجل؟") },
            confirmButton = {
                TextButton(onClick = { confirmDelete = false; vm.delete(onBack) }) {
                    Text("حذف", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("إلغاء") } }
        )
    }
}

@Composable
private fun Field(
    label: String,
    value: String,
    lines: Int = 1,
    keyboard: KeyboardType = KeyboardType.Text,
    onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        singleLine = lines == 1,
        minLines = lines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard)
    )
}
