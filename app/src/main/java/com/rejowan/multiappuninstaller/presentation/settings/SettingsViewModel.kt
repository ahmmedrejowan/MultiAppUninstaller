/*
 * Multi App Uninstaller
 * Copyright (C) 2025 K M Rejowan Ahmmed
 */

package com.rejowan.multiappuninstaller.presentation.settings

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejowan.multiappuninstaller.BuildConfig
import com.rejowan.multiappuninstaller.data.ApkDownloadManager
import com.rejowan.multiappuninstaller.data.GithubRelease
import com.rejowan.multiappuninstaller.data.UpdateCheckInterval
import com.rejowan.multiappuninstaller.data.UpdateChecker
import com.rejowan.multiappuninstaller.data.UpdateState
import com.rejowan.multiappuninstaller.repo.MainRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingsViewModel(
    private val repository: MainRepository,
    private val apkDownloadManager: ApkDownloadManager
) : ViewModel() {

    private val _theme = MutableStateFlow("System Default")
    val theme: StateFlow<String> = _theme

    private val _dynamicColorEnabled = MutableStateFlow(false)
    val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState

    private val _downloadState = MutableStateFlow<ApkDownloadManager.DownloadState>(ApkDownloadManager.DownloadState.Idle)
    val downloadState: StateFlow<ApkDownloadManager.DownloadState> = _downloadState

    private val _updateCheckInterval = MutableStateFlow(UpdateCheckInterval.WEEKLY)
    val updateCheckInterval: StateFlow<UpdateCheckInterval> = _updateCheckInterval

    private var downloadJob: Job? = null

    fun loadTheme() {
        viewModelScope.launch {
            repository.getTheme().collect { _theme.value = it }
        }
    }

    fun saveTheme(theme: String) {
        viewModelScope.launch {
            repository.saveTheme(theme)
            _theme.value = theme
        }
    }

    fun setDefaultThemeIfNotSet() {
        viewModelScope.launch {
            repository.setDefaultThemeIfNotSet()
            repository.getTheme().collect { _theme.value = it }
        }
    }

    fun loadDynamicColorPreference() {
        viewModelScope.launch {
            repository.isDynamicColorEnabled().collect { _dynamicColorEnabled.value = it }
        }
    }

    fun saveDynamicColorPreference(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveDynamicColorPreference(enabled)
            _dynamicColorEnabled.value = enabled
        }
    }

    // ========== Update System ==========

    fun checkForUpdates() {
        viewModelScope.launch {
            _updateState.value = UpdateState.Checking
            val currentVersion = BuildConfig.VERSION_NAME

            val result = UpdateChecker.checkForUpdate(currentVersion)
            result.fold(
                onSuccess = { release ->
                    _updateState.value = if (release != null) {
                        UpdateState.Available(release, currentVersion)
                    } else {
                        UpdateState.UpToDate
                    }
                },
                onFailure = { e ->
                    Timber.e(e, "Update check failed")
                    _updateState.value = UpdateState.Error(e.message ?: "Unknown error")
                }
            )
        }
    }

    fun dismissUpdateDialog() {
        _updateState.value = UpdateState.Idle
    }

    fun skipVersion(version: String) {
        // For simplicity, just dismiss. A full implementation would persist skipped versions in DataStore.
        _updateState.value = UpdateState.Idle
        Timber.d("Skipped version: $version")
    }

    fun getApkDownloadUrl(release: GithubRelease): String? {
        return release.apkDownloadUrl
    }

    fun startDownload(release: GithubRelease) {
        val url = release.apkDownloadUrl ?: return
        val fileName = "MultiAppUninstaller-v${release.version}.apk"

        downloadJob?.cancel()
        downloadJob = viewModelScope.launch {
            apkDownloadManager.downloadApk(url, fileName, release.version).collect { state ->
                _downloadState.value = state
            }
        }
    }

    fun cancelDownload() {
        downloadJob?.cancel()
        _downloadState.value = ApkDownloadManager.DownloadState.Cancelled
    }

    fun resetDownloadState() {
        _downloadState.value = ApkDownloadManager.DownloadState.Idle
    }

    fun installDownloadedApk() {
        val state = _downloadState.value
        if (state is ApkDownloadManager.DownloadState.Completed) {
            apkDownloadManager.installApk(state.file)
        }
    }

    fun canInstallApks(): Boolean = apkDownloadManager.canInstallApks()

    fun openInstallPermissionSettings(): Intent? = apkDownloadManager.getInstallPermissionIntent()

    fun setUpdateCheckInterval(interval: UpdateCheckInterval) {
        _updateCheckInterval.value = interval
    }
}
