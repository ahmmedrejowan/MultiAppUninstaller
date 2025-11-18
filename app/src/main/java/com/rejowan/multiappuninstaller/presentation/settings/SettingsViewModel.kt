package com.rejowan.multiappuninstaller.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejowan.multiappuninstaller.BuildConfig
import com.rejowan.multiappuninstaller.repo.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * ViewModel for Settings screen
 * Manages theme and dynamic color preferences
 */
class SettingsViewModel(
    private val repository: MainRepository
) : ViewModel() {

    private val _theme = MutableStateFlow("System Default")
    val theme: StateFlow<String> = _theme

    private val _dynamicColorEnabled = MutableStateFlow(false)
    val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled

    /**
     * Loads theme preference from repository
     */
    fun loadTheme() {
        viewModelScope.launch {
            repository.getTheme().collect { theme ->
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
            repository.saveTheme(theme)
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
            repository.setDefaultThemeIfNotSet()
            repository.getTheme().collect { theme ->
                _theme.value = theme
            }
        }
    }

    /**
     * Loads dynamic color preference
     */
    fun loadDynamicColorPreference() {
        viewModelScope.launch {
            repository.isDynamicColorEnabled().collect { enabled ->
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
            repository.saveDynamicColorPreference(enabled)
            _dynamicColorEnabled.value = enabled
            if (BuildConfig.DEBUG) {
                Timber.d("Dynamic color preference saved: $enabled")
            }
        }
    }
}
