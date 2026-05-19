package com.southintel.archive.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore("archive_prefs")

@Singleton
class AuthStore @Inject constructor(private val context: Context) {
    private object Keys {
        val TOKEN = stringPreferencesKey("token")
        val EMAIL = stringPreferencesKey("email")
        val DARK = booleanPreferencesKey("dark_mode")
        val CLOUD = booleanPreferencesKey("cloud_sync")
    }
    val token: Flow<String?> = context.dataStore.data.map { it[Keys.TOKEN] }
    val email: Flow<String?> = context.dataStore.data.map { it[Keys.EMAIL] }
    val darkMode: Flow<Boolean> = context.dataStore.data.map { it[Keys.DARK] ?: true }
    val cloudSync: Flow<Boolean> = context.dataStore.data.map { it[Keys.CLOUD] ?: true }

    suspend fun tokenBlocking(): String? = token.first()

    suspend fun setSession(token: String, email: String) {
        context.dataStore.edit { it[Keys.TOKEN] = token; it[Keys.EMAIL] = email }
    }
    suspend fun clearSession() {
        context.dataStore.edit { it.remove(Keys.TOKEN); it.remove(Keys.EMAIL) }
    }
    suspend fun setDark(v: Boolean) = context.dataStore.edit { it[Keys.DARK] = v }
    suspend fun setCloud(v: Boolean) = context.dataStore.edit { it[Keys.CLOUD] = v }
}
