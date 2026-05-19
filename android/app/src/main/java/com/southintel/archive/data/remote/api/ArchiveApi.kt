package com.southintel.archive.data.remote.api

import com.southintel.archive.data.remote.dto.*
import retrofit2.http.*

interface ArchiveApi {
    @POST("api/auth/register")
    suspend fun register(@Body body: AuthRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body body: AuthRequest): AuthResponse

    @GET("api/records")
    suspend fun list(@Query("since") since: String? = null): SyncPullResponse

    @POST("api/records/sync")
    suspend fun pushSync(@Body body: SyncPushBody): GenericOk
}
