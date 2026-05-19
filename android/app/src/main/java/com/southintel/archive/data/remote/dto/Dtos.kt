package com.southintel.archive.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(val email: String, val password: String, val displayName: String? = null)

@Serializable
data class UserDto(val id: String, val email: String, val displayName: String = "")

@Serializable
data class AuthResponse(val token: String, val user: UserDto)

@Serializable
data class RecordDto(
    @SerialName("_id") val id: String,
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
    val clientUpdatedAt: String // ISO-8601
)

@Serializable
data class SyncPushBody(val records: List<RecordDto>)

@Serializable
data class SyncPullResponse(val records: List<RecordDto>, val serverTime: String)

@Serializable
data class GenericOk(val ok: Boolean = true)
