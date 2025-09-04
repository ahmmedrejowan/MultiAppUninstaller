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

    fun loadApps() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _apps.value = mainRepository.getAppList()
            } catch (e: Exception) {
                Timber.tag("MainViewModel").e(e, "Error loading apps")
                _error.value = "Failed to load apps: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun setError(message: String) {
        _error.value = message
    }
}