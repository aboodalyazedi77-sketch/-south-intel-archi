package com.southintel.archive.data.repository

import com.southintel.archive.data.AuthStore
import com.southintel.archive.data.local.dao.RecordDao
import com.southintel.archive.data.remote.api.ArchiveApi
import com.southintel.archive.data.remote.dto.AuthRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: ArchiveApi,
    private val store: AuthStore,
    private val dao: RecordDao,
) {
    suspend fun login(email: String, password: String) {
        val r = api.login(AuthRequest(email, password))
        store.setSession(r.token, r.user.email)
    }
    suspend fun register(email: String, password: String, name: String) {
        val r = api.register(AuthRequest(email, password, name))
        store.setSession(r.token, r.user.email)
    }
    suspend fun logout() {
        store.clearSession()
        dao.clear()
    }
}
