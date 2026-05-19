package com.southintel.archive.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.southintel.archive.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isRegister: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
) : ViewModel() {
    val state = MutableStateFlow(AuthUiState())

    fun toggle() { state.value = state.value.copy(isRegister = !state.value.isRegister, error = null) }

    fun submit(email: String, password: String, name: String, onDone: () -> Unit) {
        if (email.isBlank() || password.length < 6) {
            state.value = state.value.copy(error = "أدخل بريداً صحيحاً وكلمة مرور (6 أحرف على الأقل)")
            return
        }
        state.value = state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                if (state.value.isRegister) repo.register(email.trim(), password, name.trim())
                else repo.login(email.trim(), password)
                state.value = state.value.copy(loading = false)
                onDone()
            } catch (e: Exception) {
                state.value = state.value.copy(loading = false, error = e.message ?: "فشل")
            }
        }
    }
}
