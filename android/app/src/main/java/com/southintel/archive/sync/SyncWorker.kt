package com.southintel.archive.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.southintel.archive.data.repository.RecordRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val repo: RecordRepository,
) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result = try {
        repo.sync(); Result.success()
    } catch (e: Exception) { Result.retry() }

    companion object {
        const val UNIQUE = "sync_unique"
        fun enqueueOnce(ctx: Context) {
            val req = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(ctx)
                .enqueueUniqueWork(UNIQUE, ExistingWorkPolicy.REPLACE, req)
        }
        fun enqueuePeriodic(ctx: Context) {
            val req = PeriodicWorkRequestBuilder<SyncWorker>(2, TimeUnit.HOURS)
                .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED).build()).build()
            WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
                "sync_periodic", ExistingPeriodicWorkPolicy.KEEP, req)
        }
    }
}
