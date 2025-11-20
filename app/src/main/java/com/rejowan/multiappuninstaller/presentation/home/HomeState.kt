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
