package com.rejowan.multiappuninstaller.vm

import android.content.pm.PackageInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejowan.multiappuninstaller.repo.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(
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

    fun loadApps() {
        viewModelScope.launch {
            _loading.value = true
            Timber.tag("MainViewModel").e("Starting to load apps")
            try {
                val startTime = System.currentTimeMillis()
                _apps.value = mainRepository.getAppList()
                val duration = System.currentTimeMillis() - startTime
                Timber.tag("MainViewModel").e("Apps loaded in %d ms", duration)
            } catch (e: Exception) {
                Timber.tag("MainViewModel").e(e, "Error loading apps")
                _error.value = "Failed to load apps: ${e.message}"
            } finally {
                _loading.value = false
                Timber.tag("MainViewModel").e("Loading state set to false")
            }
        }
    }

    fun removeAppByPackageName(packageName: String) {
        _apps.value = _apps.value.filterNot { it.packageName == packageName }
    }

    fun setError(message: String) {
        _error.value = message
    }

    fun checkFirstLaunch() {
        viewModelScope.launch {
            val result = mainRepository.isFirstLaunch()
            _isFirstLaunch.value = result
            Timber.tag("MainViewModel").e("Is first launch: $result")
        }
    }

    fun setFirstLaunchDone() {
        viewModelScope.launch {
            _isFirstLaunch.value = false
            mainRepository.setFirstLaunchDone()
        }
    }

}