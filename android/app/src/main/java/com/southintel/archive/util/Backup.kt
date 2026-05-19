package com.southintel.archive.util

import android.content.Context
import android.os.Environment
import java.io.File

object Backup {
    fun backupFile(ctx: Context): File {
        val src = ctx.getDatabasePath("archive.db")
        val dir = ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: ctx.filesDir
        if (!dir.exists()) dir.mkdirs()
        val dst = File(dir, "archive-backup.db")
        src.copyTo(dst, overwrite = true)
        return dst
    }
    fun restoreFile(ctx: Context): Boolean {
        val dir = ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: ctx.filesDir
        val src = File(dir, "archive-backup.db")
        if (!src.exists()) return false
        val dst = ctx.getDatabasePath("archive.db")
        src.copyTo(dst, overwrite = true)
        return true
    }
}
