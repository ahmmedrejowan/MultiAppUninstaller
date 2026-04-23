/*
 * Multi App Uninstaller
 * Copyright (C) 2025 K M Rejowan Ahmmed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.rejowan.multiappuninstaller.data

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import timber.log.Timber
import java.io.File

class ApkDownloadManager(private val context: Context) {

    companion object {
        private const val TAG = "ApkDownloader"
        private const val UPDATES_DIR = "updates"
        private const val VERSION_FILE = "pending_version.txt"
        private const val PROGRESS_POLL_INTERVAL = 500L
    }

    private val downloadManager: DownloadManager? = context.getSystemService()

    sealed class DownloadState {
        data object Idle : DownloadState()
        data object Starting : DownloadState()
        data class Downloading(val progress: Int, val downloadedBytes: Long, val totalBytes: Long) : DownloadState()
        data class Completed(val file: File) : DownloadState()
        data class Failed(val reason: String) : DownloadState()
        data object Cancelled : DownloadState()
    }

    fun downloadApk(url: String, fileName: String, version: String? = null): Flow<DownloadState> = callbackFlow {
        version?.let { savePendingVersion(it) }
        Timber.tag(TAG).d("Download start: $url -> $fileName")

        if (downloadManager == null) {
            trySend(DownloadState.Failed("Download manager not available"))
            close()
            return@callbackFlow
        }

        trySend(DownloadState.Starting)

        val updatesDir = File(context.getExternalFilesDir(null), UPDATES_DIR)
        if (!updatesDir.exists()) updatesDir.mkdirs()

        val targetFile = File(updatesDir, fileName)
        if (targetFile.exists()) targetFile.delete()

        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle("Downloading Update")
            setDescription("Multi App Uninstaller v${version ?: ""}")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            setDestinationInExternalFilesDir(context, null, "$UPDATES_DIR/$fileName")
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }

        val downloadId = downloadManager.enqueue(request)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    downloadManager.query(query)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                            when (status) {
                                DownloadManager.STATUS_SUCCESSFUL -> {
                                    val localUri = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
                                    val actualFile = localUri?.let { uri -> Uri.parse(uri).path?.let { File(it) } } ?: targetFile
                                    trySend(DownloadState.Completed(actualFile))
                                }
                                DownloadManager.STATUS_FAILED -> {
                                    val reason = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                                    trySend(DownloadState.Failed(getFailureReason(reason)))
                                }
                            }
                        }
                    }
                    close()
                }
            }
        }

        ContextCompat.registerReceiver(context, receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), ContextCompat.RECEIVER_NOT_EXPORTED)

        while (isActive) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            downloadManager.query(query)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    val bytesDownloaded = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val bytesTotal = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                    when (status) {
                        DownloadManager.STATUS_RUNNING -> {
                            val progress = if (bytesTotal > 0) ((bytesDownloaded * 100) / bytesTotal).toInt() else 0
                            trySend(DownloadState.Downloading(progress, bytesDownloaded, bytesTotal))
                        }
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            val localUri = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
                            val actualFile = localUri?.let { uri -> Uri.parse(uri).path?.let { File(it) } } ?: targetFile
                            trySend(DownloadState.Completed(actualFile))
                            close()
                            return@use
                        }
                        DownloadManager.STATUS_FAILED -> {
                            val reason = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                            trySend(DownloadState.Failed(getFailureReason(reason)))
                            close()
                            return@use
                        }
                    }
                }
            }
            delay(PROGRESS_POLL_INTERVAL)
        }

        awaitClose {
            try { context.unregisterReceiver(receiver) } catch (_: Exception) {}
        }
    }

    fun cancelDownload(downloadId: Long) {
        downloadManager?.remove(downloadId)
    }

    fun installApk(file: File): Boolean {
        if (!file.exists()) return false
        return try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to launch install intent")
            false
        }
    }

    fun canInstallApks(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else true
    }

    fun getInstallPermissionIntent(): Intent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        } else null
    }

    fun cleanupOldDownloads() {
        val updatesDir = File(context.getExternalFilesDir(null), UPDATES_DIR)
        if (updatesDir.exists()) {
            updatesDir.listFiles()?.forEach { if (it.name.endsWith(".apk")) it.delete() }
        }
        clearPendingVersion()
    }

    fun getPendingApk(): File? {
        val updatesDir = File(context.getExternalFilesDir(null), UPDATES_DIR)
        if (!updatesDir.exists()) return null
        return updatesDir.listFiles()?.filter { it.name.endsWith(".apk") && it.exists() && it.length() > 0 }?.maxByOrNull { it.lastModified() }
    }

    fun hasPendingApk(currentAppVersion: String): Boolean {
        val pendingVersion = getPendingApkVersion() ?: return false
        if (!isNewerVersion(pendingVersion, currentAppVersion)) {
            cleanupOldDownloads()
            return false
        }
        return true
    }

    fun getPendingApkVersion(): String? {
        val updatesDir = File(context.getExternalFilesDir(null), UPDATES_DIR)
        val versionFile = File(updatesDir, VERSION_FILE)
        return if (versionFile.exists()) versionFile.readText().trim()
        else getPendingApk()?.name?.let { extractVersionFromFileName(it) }
    }

    private fun savePendingVersion(version: String) {
        val updatesDir = File(context.getExternalFilesDir(null), UPDATES_DIR)
        if (!updatesDir.exists()) updatesDir.mkdirs()
        File(updatesDir, VERSION_FILE).writeText(version)
    }

    private fun clearPendingVersion() {
        val updatesDir = File(context.getExternalFilesDir(null), UPDATES_DIR)
        val versionFile = File(updatesDir, VERSION_FILE)
        if (versionFile.exists()) versionFile.delete()
    }

    private fun isNewerVersion(newVersion: String, currentVersion: String): Boolean {
        val newParts = newVersion.removePrefix("v").split("-")[0].split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = currentVersion.removePrefix("v").split("-")[0].split(".").mapNotNull { it.toIntOrNull() }
        for (i in 0 until maxOf(newParts.size, currentParts.size)) {
            val n = newParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (n > c) return true
            if (n < c) return false
        }
        return false
    }

    private fun extractVersionFromFileName(fileName: String): String? {
        val regex = Regex("""v?(\d+\.\d+\.\d+)""")
        return regex.find(fileName)?.groupValues?.get(1)
    }

    private fun getFailureReason(reason: Int): String = when (reason) {
        DownloadManager.ERROR_CANNOT_RESUME -> "Cannot resume download"
        DownloadManager.ERROR_DEVICE_NOT_FOUND -> "Storage not found"
        DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "File already exists"
        DownloadManager.ERROR_FILE_ERROR -> "Storage error"
        DownloadManager.ERROR_HTTP_DATA_ERROR -> "Network data error"
        DownloadManager.ERROR_INSUFFICIENT_SPACE -> "Insufficient storage space"
        DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "Too many redirects"
        DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "HTTP error"
        DownloadManager.ERROR_UNKNOWN -> "Unknown error"
        else -> "Download failed (code: $reason)"
    }
}
