package com.southintel.archive.ui.screens.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.southintel.archive.data.local.entity.RecordEntity
import com.southintel.archive.data.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val repo: RecordRepository
) : ViewModel() {

    val state = MutableStateFlow<RecordEntity?>(null)
    val loaded = MutableStateFlow(false)

    fun load(id: String?) {
        if (loaded.value) return
        viewModelScope.launch {
            state.value = if (id.isNullOrBlank()) {
                val now = System.currentTimeMillis()
                RecordEntity(id = UUID.randomUUID().toString(), createdAt = now, updatedAt = now)
            } else repo.get(id) ?: run {
                val now = System.currentTimeMillis()
                RecordEntity(id = id, createdAt = now, updatedAt = now)
            }
            loaded.value = true
        }
    }

    fun update(transform: (RecordEntity) -> RecordEntity) {
        state.value = state.value?.let(transform)
    }

    fun save(onDone: () -> Unit) {
        val r = state.value ?: return
        viewModelScope.launch { repo.save(r); onDone() }
    }

    fun delete(onDone: () -> Unit) {
        val r = state.value ?: return
        viewModelScope.launch { repo.delete(r.id); onDone() }
    }
}
