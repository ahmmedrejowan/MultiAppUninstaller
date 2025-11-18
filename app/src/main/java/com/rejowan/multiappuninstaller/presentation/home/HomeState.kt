package com.rejowan.multiappuninstaller.presentation.home

import android.content.pm.PackageInfo

/**
 * Represents the UI state for the Home screen.
 */
data class HomeState(
    val apps: List<PackageInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val sortType: SortType = SortType.NAME,
    val sortAscending: Boolean = true,
    val selectedApps: Set<String> = emptySet(),
    val isSelectionMode: Boolean = false,
    val isRefreshing: Boolean = false,

    // Uninstall related state
    val isUninstalling: Boolean = false,
    val uninstallQueue: List<String> = emptyList(),
    val totalSelectedAtStart: Int = 0,
    val succeededCount: Int = 0,
    val failedPackages: List<String> = emptyList(),

    // Dialog states
    val showExitDialog: Boolean = false,
    val showCancelConfirmationDialog: Boolean = false,
    val showUninstallConfirmDialog: Boolean = false,
    val showBatchResultDialog: Boolean = false,

    // First launch tutorial
    val showFirstTutorial: Boolean = false
)

/**
 * Sort types for app list
 */
enum class SortType {
    NAME,
    SIZE,
    INSTALL_DATE,
    UPDATE_DATE
}
