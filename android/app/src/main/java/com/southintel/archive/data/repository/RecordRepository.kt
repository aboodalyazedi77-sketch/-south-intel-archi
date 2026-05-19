package com.southintel.archive.data.repository

import com.southintel.archive.data.AuthStore
import com.southintel.archive.data.local.dao.RecordDao
import com.southintel.archive.data.local.entity.RecordEntity
import com.southintel.archive.data.remote.api.ArchiveApi
import com.southintel.archive.data.remote.dto.RecordDto
import com.southintel.archive.data.remote.dto.SyncPushBody
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordRepository @Inject constructor(
    private val dao: RecordDao,
    private val api: ArchiveApi,
    private val auth: AuthStore,
) {
    fun observeAll(): Flow<List<RecordEntity>> = dao.observeAll()
    fun search(q: String) = dao.search(q)
    fun filter(name: String, gov: String, aff: String, from: Long, to: Long) =
        dao.filter(name, gov, aff, from, to)
    fun count() = dao.count()
    fun governorates() = dao.governorates()
    fun affiliations() = dao.affiliations()
    suspend fun get(id: String) = dao.getById(id)

    suspend fun save(r: RecordEntity) {
        val now = System.currentTimeMillis()
        dao.upsert(r.copy(updatedAt = now, pendingSync = true))
    }

    suspend fun delete(id: String) {
        val existing = dao.getById(id) ?: return
        dao.upsert(existing.copy(deleted = true, updatedAt = System.currentTimeMillis(), pendingSync = true))
    }

    suspend fun clearLocal() = dao.clear()

    /** Two-way sync: pull since last known, push pending. Returns # synced. */
    suspend fun sync(): Int {
        if (auth.tokenBlocking().isNullOrBlank()) return 0
        // Pull
        val lastLocal = dao.maxUpdatedAt() ?: 0L
        val since = if (lastLocal == 0L) null else isoFmt.format(Date(lastLocal))
        val pulled = api.list(since = since).records
        if (pulled.isNotEmpty()) {
            dao.upsertAll(pulled.map { it.toEntity().copy(pendingSync = false) })
        }
        // Push
        val pending = dao.pending()
        if (pending.isNotEmpty()) {
            api.pushSync(SyncPushBody(pending.map { it.toDto() }))
            dao.markSynced(pending.map { it.id })
        }
        return pulled.size + pending.size
    }

    companion object {
        private val isoFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("UTC") }

        fun RecordEntity.toDto() = RecordDto(
            id = id, name = name, kunya = kunya, nationality = nationality,
            governorate = governorate, residence = residence, work = work, age = age,
            qualification = qualification, affiliation = affiliation,
            information = information, notes = notes, deleted = deleted,
            clientUpdatedAt = isoFmt.format(Date(updatedAt))
        )

        fun RecordDto.toEntity(): RecordEntity {
            val ts = runCatching { isoFmt.parse(clientUpdatedAt)?.time ?: System.currentTimeMillis() }
                .getOrDefault(System.currentTimeMillis())
            return RecordEntity(
                id = id, name = name, kunya = kunya, nationality = nationality,
                governorate = governorate, residence = residence, work = work, age = age,
                qualification = qualification, affiliation = affiliation,
                information = information, notes = notes, deleted = deleted,
                createdAt = ts, updatedAt = ts, pendingSync = false
            )
        }
    }
}
