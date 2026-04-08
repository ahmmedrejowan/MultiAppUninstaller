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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class GithubRelease(
    val tagName: String,
    val name: String,
    val body: String,
    val htmlUrl: String,
    val publishedAt: String,
    val apkDownloadUrl: String?,
    val apkSize: Long?
) {
    val version: String get() = tagName.removePrefix("v")
}

sealed class UpdateState {
    data object Idle : UpdateState()
    data object Checking : UpdateState()
    data class Available(val release: GithubRelease, val currentVersion: String) : UpdateState()
    data object UpToDate : UpdateState()
    data class Error(val message: String) : UpdateState()
}

enum class UpdateCheckInterval(val displayName: String, val days: Int) {
    NEVER("Never", -1),
    DAILY("Daily", 1),
    EVERY_3_DAYS("Every 3 days", 3),
    WEEKLY("Weekly", 7),
    EVERY_2_WEEKS("Every 2 weeks", 14),
    MONTHLY("Monthly", 30)
}

object UpdateChecker {

    private const val OWNER = "ahmmedrejowan"
    private const val REPO = "MultiAppUninstaller"

    suspend fun checkForUpdate(currentVersion: String): Result<GithubRelease?> = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.github.com/repos/$OWNER/$REPO/releases/latest")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            if (connection.responseCode != 200) {
                return@withContext Result.failure(Exception("HTTP ${connection.responseCode}"))
            }

            val response = connection.inputStream.bufferedReader().readText()
            val json = JSONObject(response)

            val tagName = json.optString("tag_name", "")
            val remoteVersion = tagName.removePrefix("v")

            if (remoteVersion.isEmpty()) {
                return@withContext Result.success(null)
            }

            val assets = json.optJSONArray("assets") ?: JSONArray()
            var apkUrl: String? = null
            var apkSize: Long? = null
            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                if (asset.optString("name", "").endsWith(".apk")) {
                    apkUrl = asset.optString("browser_download_url")
                    apkSize = asset.optLong("size", 0)
                    break
                }
            }

            val release = GithubRelease(
                tagName = tagName,
                name = json.optString("name", ""),
                body = json.optString("body", ""),
                htmlUrl = json.optString("html_url", ""),
                publishedAt = json.optString("published_at", ""),
                apkDownloadUrl = apkUrl,
                apkSize = apkSize
            )

            if (isNewerVersion(remoteVersion, currentVersion)) {
                Result.success(release)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isNewerVersion(remote: String, current: String): Boolean {
        val remoteParts = remote.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = current.split(".").mapNotNull { it.toIntOrNull() }
        val maxLen = maxOf(remoteParts.size, currentParts.size)
        for (i in 0 until maxLen) {
            val r = remoteParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }
}
