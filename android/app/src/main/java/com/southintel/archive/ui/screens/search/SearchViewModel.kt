package com.southintel.archive.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.southintel.archive.data.local.entity.RecordEntity
import com.southintel.archive.data.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SearchFilters(
    val q: String = "",
    val governorate: String = "",
    val affiliation: String = "",
    val fromTs: Long = 0L,
    val toTs: Long = 0L,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: RecordRepository,
) : ViewModel() {

    val filters = MutableStateFlow(SearchFilters())
    val governorates = repo.governorates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val affiliations = repo.affiliations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val results: StateFlow<List<RecordEntity>> = filters.flatMapLatest { f ->
        if (f.q.isNotBlank() && f.governorate.isBlank() && f.affiliation.isBlank() && f.fromTs == 0L && f.toTs == 0L)
            repo.search(f.q)
        else
            repo.filter(f.q, f.governorate, f.affiliation, f.fromTs, f.toTs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun update(transform: (SearchFilters) -> SearchFilters) {
        filters.value = transform(filters.value)
    }
}
