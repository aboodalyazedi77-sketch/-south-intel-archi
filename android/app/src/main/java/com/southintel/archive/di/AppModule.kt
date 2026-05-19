package com.southintel.archive.di

import android.content.Context
import androidx.room.Room
import com.southintel.archive.BuildConfig
import com.southintel.archive.data.AuthStore
import com.southintel.archive.data.local.ArchiveDatabase
import com.southintel.archive.data.local.dao.RecordDao
import com.southintel.archive.data.remote.api.ArchiveApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun db(@ApplicationContext ctx: Context): ArchiveDatabase =
        Room.databaseBuilder(ctx, ArchiveDatabase::class.java, ArchiveDatabase.NAME)
            .fallbackToDestructiveMigration().build()

    @Provides fun dao(db: ArchiveDatabase): RecordDao = db.recordDao()

    @Provides @Singleton
    fun authStore(@ApplicationContext ctx: Context) = AuthStore(ctx)

    @Provides @Singleton
    fun api(store: AuthStore): ArchiveApi {
        val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = runBlocking { store.tokenBlocking() }
                val req = chain.request().newBuilder().apply {
                    if (!token.isNullOrBlank()) addHeader("Authorization", "Bearer $token")
                }.build()
                chain.proceed(req)
            }
            .addInterceptor(logging)
            .build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ArchiveApi::class.java)
    }
}
