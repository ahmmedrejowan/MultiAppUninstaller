/*
 * Multi App Uninstaller
 * Copyright (C) 2025 K M Rejowan Ahmmed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.rejowan.multiappuninstaller.repoImpl

import com.rejowan.multiappuninstaller.data.GithubRelease
import com.rejowan.multiappuninstaller.data.UpdateCheckInterval
import com.rejowan.multiappuninstaller.data.UpdatePrefHelper
import com.rejowan.multiappuninstaller.data.UpdateState
import com.rejowan.multiappuninstaller.repo.UpdateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL

class UpdateRepositoryImpl(
    private val prefHelper: UpdatePrefHelper
) : UpdateRepository {

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    override val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    override suspend fun runCheckIfNeeded(currentVersion: String) {
        val interval = prefHelper.getUpdateCheckInterval().first()
        if (interval == UpdateCheckInterval.NEVER) {
            Timber.d("Auto update check disabled")
            return
        }

        val lastCheck = prefHelper.getLastCheckTime()
        val intervalMillis = interval.days * 24L * 60L * 60L * 1000L
        val elapsed = System.currentTimeMillis() - lastCheck

        if (elapsed >= intervalMillis) {
            Timber.d("Auto-checking for updates (last check: ${elapsed / 1000 / 60 / 60}h ago)")
            runCheck(currentVersion)
        } else {
            val hoursLeft = (intervalMillis - elapsed) / 1000 / 60 / 60
            Timber.d("Next auto-check in ${hoursLeft}h")
        }
    }

    override suspend fun runCheck(currentVersion: String) {
        _updateState.value = UpdateState.Checking
        val result = fetchLatestRelease(currentVersion)
        result.fold(
            onSuccess = { release ->
                _updateState.value = when {
                    release == null -> UpdateState.UpToDate
                    prefHelper.shouldSkipVersion(release.version) -> {
                        Timber.d("Version ${release.version} is skipped")
                        UpdateState.UpToDate
                    }
                    else -> UpdateState.Available(release, currentVersion)
                }
            },
            onFailure = { e ->
                Timber.e(e, "Update check failed")
                _updateState.value = UpdateState.Error(e.message ?: "Unknown error")
            }
        )
        prefHelper.setLastCheckTime(System.currentTimeMillis())
    }

    override fun dismissUpdate() {
        _updateState.value = UpdateState.Idle
    }

    override suspend fun getLastCheckTime(): Long = prefHelper.getLastCheckTime()

    override suspend fun shouldSkipVersion(version: String): Boolean =
        prefHelper.shouldSkipVersion(version)

    override suspend fun skipVersion(version: String) {
        prefHelper.skipVersion(version)
        _updateState.value = UpdateState.Idle
    }

    override suspend fun clearSkippedVersions() = prefHelper.clearSkippedVersions()

    override fun getUpdateCheckInterval(): Flow<UpdateCheckInterval> =
        prefHelper.getUpdateCheckInterval()

    override suspend fun setUpdateCheckInterval(interval: UpdateCheckInterval) =
        prefHelper.setUpdateCheckInterval(interval)

    private suspend fun fetchLatestRelease(currentVersion: String): Result<GithubRelease?> =
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://api.github.com/repos/$OWNER/$REPO/releases/latest")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Accept", "application/vnd.github+json")
                connection.setRequestProperty("User-Agent", "MultiAppUninstaller-Android")
                connection.connectTimeout = 10_000
                connection.readTimeout = 10_000

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
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
                Timber.e(e, "Update check failed")
                Result.failure(e)
            }
        }

    private fun isNewerVersion(remote: String, current: String): Boolean {
        return try {
            val remoteParts = parseVersion(remote)
            val currentParts = parseVersion(current)
            val maxLen = maxOf(remoteParts.size, currentParts.size)
            for (i in 0 until maxLen) {
                val r = remoteParts.getOrElse(i) { 0 }
                val c = currentParts.getOrElse(i) { 0 }
                if (r > c) return true
                if (r < c) return false
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    private fun parseVersion(version: String): List<Int> {
        return version
            .removePrefix("v")
            .removePrefix("V")
            .split("-")[0]
            .split(".")
            .mapNotNull { it.toIntOrNull() }
    }

    companion object {
        private const val OWNER = "ahmmedrejowan"
        private const val REPO = "MultiAppUninstaller"
    }
}
