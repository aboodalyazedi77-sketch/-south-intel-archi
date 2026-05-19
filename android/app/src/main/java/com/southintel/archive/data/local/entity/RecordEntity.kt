package com.southintel.archive.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class RecordEntity(
    @PrimaryKey val id: String,
    val name: String = "",
    val kunya: String = "",
    val nationality: String = "",
    val governorate: String = "",
    val residence: String = "",
    val work: String = "",
    val age: Int? = null,
    val qualification: String = "",
    val affiliation: String = "",
    val information: String = "",
    val notes: String = "",
    val deleted: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
    val pendingSync: Boolean = true
)
