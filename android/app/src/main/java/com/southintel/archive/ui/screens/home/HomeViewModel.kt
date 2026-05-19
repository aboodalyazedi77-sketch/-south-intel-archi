package com.southintel.archive.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.southintel.archive.data.local.entity.RecordEntity
import com.southintel.archive.data.repository.RecordRepository
import com.southintel.archive.sync.SyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    app: Application,
    private val repo: RecordRepository,
) : AndroidViewModel(app) {

    val records: StateFlow<List<RecordEntity>> = repo.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val count: StateFlow<Int> = repo.count()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val governorates: StateFlow<List<String>> = repo.governorates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        SyncWorker.enqueueOnce(getApplication())
        SyncWorker.enqueuePeriodic(getApplication())
    }

    fun syncNow() {
        viewModelScope.launch { runCatching { repo.sync() } }
        SyncWorker.enqueueOnce(getApplication())
    }
}
