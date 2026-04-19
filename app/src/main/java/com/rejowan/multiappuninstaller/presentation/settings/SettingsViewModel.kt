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
import com.rejowan.multiappuninstaller.data.UpdateState
import com.rejowan.multiappuninstaller.repo.MainRepository
import com.rejowan.multiappuninstaller.repo.UpdateRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingsViewModel(
    private val repository: MainRepository,
    private val updateRepository: UpdateRepository,
    private val apkDownloadManager: ApkDownloadManager
) : ViewModel() {

    private val _theme = MutableStateFlow("System Default")
    val theme: StateFlow<String> = _theme

    private val _dynamicColorEnabled = MutableStateFlow(false)
    val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled

    val updateState: StateFlow<UpdateState> = updateRepository.updateState

    private val _downloadState =
        MutableStateFlow<ApkDownloadManager.DownloadState>(ApkDownloadManager.DownloadState.Idle)
    val downloadState: StateFlow<ApkDownloadManager.DownloadState> = _downloadState

    private val _updateCheckInterval = MutableStateFlow(UpdateCheckInterval.WEEKLY)
    val updateCheckInterval: StateFlow<UpdateCheckInterval> = _updateCheckInterval

    private var downloadJob: Job? = null

    init {
        observeUpdateCheckInterval()
        checkPendingApk()
    }

    private fun observeUpdateCheckInterval() {
        viewModelScope.launch {
            updateRepository.getUpdateCheckInterval().collect { _updateCheckInterval.value = it }
        }
    }

    private fun checkPendingApk() {
        val currentVersion = BuildConfig.VERSION_NAME
        if (apkDownloadManager.hasPendingApk(currentVersion)) {
            apkDownloadManager.getPendingApk()?.let { file ->
                Timber.d("Pending APK detected: ${file.name}")
                _downloadState.value = ApkDownloadManager.DownloadState.Completed(file)
            }
        } else {
            apkDownloadManager.cleanupOldDownloads()
        }
    }

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
            updateRepository.runCheck(BuildConfig.VERSION_NAME)
        }
    }

    fun dismissUpdateDialog() {
        updateRepository.dismissUpdate()
    }

    fun skipVersion(version: String) {
        viewModelScope.launch {
            updateRepository.skipVersion(version)
            Timber.d("Skipped version: $version")
        }
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
        viewModelScope.launch {
            updateRepository.setUpdateCheckInterval(interval)
            Timber.d("Update check interval changed to: ${interval.displayName}")
        }
    }
}
