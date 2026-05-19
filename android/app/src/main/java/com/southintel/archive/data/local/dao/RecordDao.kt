package com.southintel.archive.data.local.dao

import androidx.room.*
import com.southintel.archive.data.local.entity.RecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Query("SELECT * FROM records WHERE deleted = 0 ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<RecordEntity>>

    @Query("""
        SELECT * FROM records WHERE deleted = 0 AND (
            name LIKE '%' || :q || '%' OR kunya LIKE '%' || :q || '%' OR
            nationality LIKE '%' || :q || '%' OR governorate LIKE '%' || :q || '%' OR
            residence LIKE '%' || :q || '%' OR work LIKE '%' || :q || '%' OR
            qualification LIKE '%' || :q || '%' OR affiliation LIKE '%' || :q || '%' OR
            information LIKE '%' || :q || '%' OR notes LIKE '%' || :q || '%'
        ) ORDER BY updatedAt DESC
    """)
    fun search(q: String): Flow<List<RecordEntity>>

    @Query("""
        SELECT * FROM records WHERE deleted = 0
        AND (:name = '' OR name LIKE '%' || :name || '%')
        AND (:gov = '' OR governorate = :gov)
        AND (:aff = '' OR affiliation = :aff)
        AND (:fromTs = 0 OR createdAt >= :fromTs)
        AND (:toTs = 0 OR createdAt <= :toTs)
        ORDER BY updatedAt DESC
    """)
    fun filter(name: String, gov: String, aff: String, fromTs: Long, toTs: Long): Flow<List<RecordEntity>>

    @Query("SELECT * FROM records WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): RecordEntity?

    @Query("SELECT COUNT(*) FROM records WHERE deleted = 0")
    fun count(): Flow<Int>

    @Query("SELECT DISTINCT governorate FROM records WHERE deleted = 0 AND governorate <> '' ORDER BY governorate")
    fun governorates(): Flow<List<String>>

    @Query("SELECT DISTINCT affiliation FROM records WHERE deleted = 0 AND affiliation <> '' ORDER BY affiliation")
    fun affiliations(): Flow<List<String>>

    @Query("SELECT * FROM records WHERE pendingSync = 1")
    suspend fun pending(): List<RecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(r: RecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(rs: List<RecordEntity>)

    @Query("UPDATE records SET pendingSync = 0 WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<String>)

    @Query("DELETE FROM records")
    suspend fun clear()

    @Query("SELECT MAX(updatedAt) FROM records")
    suspend fun maxUpdatedAt(): Long?
}
