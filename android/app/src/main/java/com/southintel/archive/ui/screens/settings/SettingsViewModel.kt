package com.southintel.archive.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.southintel.archive.data.AuthStore
import com.southintel.archive.data.repository.AuthRepository
import com.southintel.archive.data.repository.RecordRepository
import com.southintel.archive.sync.SyncWorker
import com.southintel.archive.util.Backup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    app: Application,
    private val store: AuthStore,
    private val authRepo: AuthRepository,
    private val repo: RecordRepository,
) : AndroidViewModel(app) {

    val darkMode = store.darkMode
    val cloudSync = store.cloudSync
    val token = store.token
    val email = store.email
    val messages = MutableSharedFlow<String>(extraBufferCapacity = 8)

    fun setDark(v: Boolean) = viewModelScope.launch { store.setDark(v) }
    fun setCloud(v: Boolean) = viewModelScope.launch { store.setCloud(v) }

    fun syncNow() = viewModelScope.launch {
        runCatching { repo.sync() }
            .onSuccess { messages.tryEmit("تمت المزامنة بنجاح") }
            .onFailure { messages.tryEmit("فشل المزامنة: ${it.message}") }
        SyncWorker.enqueueOnce(getApplication())
    }

    fun backup() = viewModelScope.launch {
        runCatching { Backup.backupFile(getApplication()) }
            .onSuccess { messages.tryEmit("تم حفظ النسخة: ${it.absolutePath}") }
            .onFailure { messages.tryEmit("فشل النسخ: ${it.message}") }
    }

    fun restore() = viewModelScope.launch {
        runCatching { Backup.restoreFile(getApplication()) }
            .onSuccess {
                if (it) messages.tryEmit("تمت الاستعادة. أعد تشغيل التطبيق.")
                else messages.tryEmit("لا يوجد ملف نسخة احتياطية")
            }
            .onFailure { messages.tryEmit("فشل الاستعادة: ${it.message}") }
    }

    fun logout(onDone: () -> Unit) = viewModelScope.launch {
        authRepo.logout(); onDone()
    }
}
