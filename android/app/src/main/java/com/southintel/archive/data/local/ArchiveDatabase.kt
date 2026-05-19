package com.southintel.archive.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.southintel.archive.data.local.dao.RecordDao
import com.southintel.archive.data.local.entity.RecordEntity

@Database(entities = [RecordEntity::class], version = 1, exportSchema = false)
abstract class ArchiveDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    companion object { const val NAME = "archive.db" }
}
