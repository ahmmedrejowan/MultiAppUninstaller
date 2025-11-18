package com.rejowan.multiappuninstaller.presentation.home

import android.content.pm.PackageInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejowan.multiappuninstaller.BuildConfig
import com.rejowan.multiappuninstaller.repo.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for Home screen
 * Manages app list, theme preferences, and first launch state
 */
class HomeViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _apps = MutableStateFlow<List<PackageInfo>>(emptyList())
    val apps: StateFlow<List<PackageInfo>> = _apps

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isFirstLaunch = MutableStateFlow<Boolean?>(null)
    val isFirstLaunch: StateFlow<Boolean?> = _isFirstLaunch

    private val _theme = MutableStateFlow("System Default")
    val theme: StateFlow<String> = _theme

    private val _dynamicColorEnabled = MutableStateFlow(false)
    val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled

    /**
     * Loads the list of installed apps from the repository
     */
    fun loadApps() {
        viewModelScope.launch {
            _loading.value = true
            if (BuildConfig.DEBUG) {
                Timber.d("Starting to load apps")
            }
            try {
                val startTime = System.currentTimeMillis()
                _apps.value = mainRepository.getAppList()
                val duration = System.currentTimeMillis() - startTime
                if (BuildConfig.DEBUG) {
                    Timber.d("Apps loaded in $duration ms")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading apps")
                _error.value = "Failed to load apps: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Removes an app from the list by package name
     * Called after successful uninstallation
     */
    fun removeAppByPackageName(packageName: String) {
        _apps.value = _apps.value.filterNot { it.packageName == packageName }
    }

    /**
     * Sets an error message to display to the user
     */
    fun setError(message: String) {
        _error.value = message
    }

    /**
     * Checks if this is the first time the app is launched
     */
    fun checkFirstLaunch() {
        viewModelScope.launch {
            val result = mainRepository.isFirstLaunch()
            _isFirstLaunch.value = result
            if (BuildConfig.DEBUG) {
                Timber.d("Is first launch: $result")
            }
        }
    }

    /**
     * Marks first launch as complete
     */
    fun setFirstLaunchDone() {
        viewModelScope.launch {
            _isFirstLaunch.value = false
            mainRepository.setFirstLaunchDone()
        }
    }

    /**
     * Loads theme preference from repository
     */
    fun loadTheme() {
        viewModelScope.launch {
            mainRepository.getTheme().collect { theme ->
                _theme.value = theme
                if (BuildConfig.DEBUG) {
                    Timber.d("Theme loaded: $theme")
                }
            }
        }
    }

    /**
     * Saves theme preference
     */
    fun saveTheme(theme: String) {
        viewModelScope.launch {
            mainRepository.saveTheme(theme)
            _theme.value = theme
            if (BuildConfig.DEBUG) {
                Timber.d("Theme saved: $theme")
            }
        }
    }

    /**
     * Sets default theme if not already set
     */
    fun setDefaultThemeIfNotSet() {
        viewModelScope.launch {
            mainRepository.setDefaultThemeIfNotSet()
            mainRepository.getTheme().collect { theme ->
                _theme.value = theme
            }
        }
    }

    /**
     * Loads dynamic color preference
     */
    fun loadDynamicColorPreference() {
        viewModelScope.launch {
            mainRepository.isDynamicColorEnabled().collect { enabled ->
                _dynamicColorEnabled.value = enabled
                if (BuildConfig.DEBUG) {
                    Timber.d("Dynamic color preference loaded: $enabled")
                }
            }
        }
    }

    /**
     * Saves dynamic color preference
     */
    fun saveDynamicColorPreference(enabled: Boolean) {
        viewModelScope.launch {
            mainRepository.saveDynamicColorPreference(enabled)
            _dynamicColorEnabled.value = enabled
            if (BuildConfig.DEBUG) {
                Timber.d("Dynamic color preference saved: $enabled")
            }
        }
    }
}