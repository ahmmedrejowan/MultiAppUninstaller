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

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rejowan.multiappuninstaller.feature.components.AppDetailsDialog
import com.rejowan.multiappuninstaller.feature.components.BatchUninstallResultDialog
import com.rejowan.multiappuninstaller.feature.components.CancelConfirmationDialog
import com.rejowan.multiappuninstaller.feature.components.ConfirmUninstallDialog
import com.rejowan.multiappuninstaller.feature.components.ExitConfirmationDialog
import com.rejowan.multiappuninstaller.feature.components.HowToUseDialog
import com.rejowan.multiappuninstaller.feature.components.SelectionBottomBar
import com.rejowan.multiappuninstaller.presentation.settings.SettingsScreen
import com.rejowan.multiappuninstaller.presentation.home.components.AppListItem
import com.rejowan.multiappuninstaller.presentation.home.components.EmptySearchState
import com.rejowan.multiappuninstaller.presentation.home.components.ErrorState
import com.rejowan.multiappuninstaller.presentation.home.components.FilterSection
import com.rejowan.multiappuninstaller.presentation.home.components.HomeTopBar
import com.rejowan.multiappuninstaller.presentation.home.components.LoadingState
import com.rejowan.multiappuninstaller.presentation.home.components.NoAppsState
import com.rejowan.multiappuninstaller.presentation.home.components.SearchSection
import com.rejowan.multiappuninstaller.receivers.AppUninstallReceiver
import com.rejowan.multiappuninstaller.utils.SortConfig
import com.rejowan.multiappuninstaller.utils.SortKey
import com.rejowan.multiappuninstaller.utils.SortOrder
import com.rejowan.multiappuninstaller.utils.sortApps
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

/**
 * Main Home Screen - Simplified and component-based
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val pm = context.packageManager
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // ViewModel state
    val appList by viewModel.apps.collectAsState()
    val appListError by viewModel.error.collectAsState()
    val appListLoading by viewModel.loading.collectAsState()
    val showFirstTutorial by viewModel.isFirstLaunch.collectAsState()

    // Permission check
    val hasPackagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.checkSelfPermission(Manifest.permission.QUERY_ALL_PACKAGES) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

    // UI State
    var searchQuery by remember { mutableStateOf("") }
    var sortType by rememberSaveable { mutableStateOf(SortType.NAME) }
    var sortAscending by rememberSaveable { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var isSettingsVisible by remember { mutableStateOf(false) }

    // LazyList state for scroll control
    val listState = rememberLazyListState()

    // Selection state
    var isSelecting by rememberSaveable { mutableStateOf(false) }
    val setSaver = listSaver<Set<String>, String>(save = { it.toList() }, restore = { it.toSet() })
    var selectedApps by rememberSaveable(stateSaver = setSaver) { mutableStateOf(emptySet()) }

    // Uninstall state
    var isUninstalling by rememberSaveable { mutableStateOf(false) }
    var uninstallQueue by rememberSaveable(stateSaver = listSaver(save = { it }, restore = { it })) {
        mutableStateOf(emptyList<String>())
    }
    val originalQueueSaver = listSaver<List<String>, String>(save = { it }, restore = { it })
    var originalQueueSnapshot by rememberSaveable(stateSaver = originalQueueSaver) { mutableStateOf(emptyList<String>()) }
    val succeededPackagesSaver = listSaver<List<String>, String>(save = { it }, restore = { it })
    var succeededPackages by rememberSaveable(stateSaver = succeededPackagesSaver) { mutableStateOf(emptyList<String>()) }
    var succeededCount by rememberSaveable { mutableIntStateOf(0) }
    val failedSaver = listSaver<List<String>, String>(save = { it }, restore = { it })
    var failedPackages by rememberSaveable(stateSaver = failedSaver) { mutableStateOf(emptyList()) }
    var isLastAppInQueue by rememberSaveable { mutableStateOf(false) }

    // Dialog states
    var showExitDialog by remember { mutableStateOf(false) }
    var showCancelConfirmationDialog by remember { mutableStateOf(false) }
    var showUninstallConfirm by remember { mutableStateOf(false) }
    var showBatchResultDialog by rememberSaveable { mutableStateOf(false) }
    var detailsFor by remember { mutableStateOf<android.content.pm.PackageInfo?>(null) }

    // Convert SortType to SortConfig
    val sortConfig = SortConfig(
        key = when (sortType) {
            SortType.NAME -> SortKey.NAME
            SortType.SIZE -> SortKey.SIZE
            SortType.INSTALL_DATE -> SortKey.INSTALLED
            SortType.UPDATE_DATE -> SortKey.UPDATED
        },
        order = if (sortAscending) SortOrder.ASC else SortOrder.DESC
    )

    // Filtered and sorted apps
    val filteredApps by remember(appList, sortConfig, searchQuery) {
        derivedStateOf {
            val filtered = appList.filter {
                it.applicationInfo?.loadLabel(pm)?.contains(searchQuery, true) == true
            }
            sortApps(filtered, pm, sortConfig)
        }
    }

    // Uninstall callback
    val onAppUninstalled: (String) -> Unit = { packageName ->
        Timber.d("╔═══════════════════════════════════════════════════════")
        Timber.d("║ 📲 onAppUninstalled CALLBACK")
        Timber.d("║ Package: $packageName")
        Timber.d("║ isSelecting: $isSelecting")
        Timber.d("║ succeededCount BEFORE: $succeededCount")
        Timber.d("║ uninstallQueue size BEFORE: ${uninstallQueue.size}")
        Timber.d("║ uninstallQueue: $uninstallQueue")

        if (!isSelecting) {
            Timber.d("║ Mode: SINGLE UNINSTALL")
            viewModel.removeAppByPackageName(packageName)
            Timber.d("║ ✅ Single app removed from list")
        } else {
            Timber.d("║ Mode: BATCH UNINSTALL")
            Timber.d("║ BroadcastReceiver role: Track success (DON'T touch queue)")

            // Track the successfully uninstalled package (don't remove from queue!)
            succeededPackages = succeededPackages + packageName
            succeededCount += 1
            Timber.d("║ ✅ Tracked $packageName as SUCCEEDED")
            Timber.d("║ Total succeeded so far: $succeededCount")
            Timber.d("║ Succeeded packages: $succeededPackages")
            Timber.d("║ Queue unchanged (Lifecycle owns it): $uninstallQueue")

            // Remove from UI
            viewModel.removeAppByPackageName(packageName)
            Timber.d("║ App removed from UI list")

            Timber.d("║ ℹ️ Lifecycle observer will handle launching next app")
        }

        Timber.d("║ succeededCount AFTER: $succeededCount")
        Timber.d("║ uninstallQueue size AFTER: ${uninstallQueue.size}")
        Timber.d("╚═══════════════════════════════════════════════════════")
    }

    // Register broadcast receiver
    val appUninstallReceiver = remember { AppUninstallReceiver(onAppUninstalled) }

    DisposableEffect(context) {
        val filter = IntentFilter(Intent.ACTION_PACKAGE_REMOVED).apply {
            addDataScheme("package")
        }
        context.registerReceiver(appUninstallReceiver, filter)
        onDispose {
            context.unregisterReceiver(appUninstallReceiver)
        }
    }


    // Load apps on launch
    if (!hasPackagePermission) {
        LaunchedEffect(Unit) { viewModel.setError("Permission not granted to access app list.") }
    } else {
        LaunchedEffect(Unit) { viewModel.loadApps() }
    }

    // Check first launch
    LaunchedEffect(Unit) {
        viewModel.checkFirstLaunch()
    }

    // Scroll to top when sort changes (not on search - user wants to see results where they are)
    LaunchedEffect(sortType, sortAscending) {
        if (listState.firstVisibleItemIndex > 0) {
            coroutineScope.launch {
                listState.scrollToItem(0) // Instant scroll, no animation (faster)
            }
        }
    }

    // Pull to refresh
    val onRefresh: () -> Unit = {
        isRefreshing = true
        coroutineScope.launch {
            viewModel.loadApps()
            delay(500)
            isRefreshing = false
        }
    }

    // Trigger first app launch when batch uninstall starts
    LaunchedEffect(isUninstalling) {
        if (isUninstalling && uninstallQueue.isNotEmpty()) {
            Timber.d("╔═══════════════════════════════════════════════════════")
            Timber.d("║ 🚀 INITIAL LAUNCH - Starting batch uninstall")

            delay(100) // Small delay to ensure state is settled

            val firstPackage = uninstallQueue.first()
            isLastAppInQueue = uninstallQueue.size == 1

            Timber.d("║ Launching first app: $firstPackage")
            Timber.d("║ Is last app? $isLastAppInQueue")

            val uninstallIntent = Intent(Intent.ACTION_DELETE).apply {
                data = "package:$firstPackage".toUri()
            }
            context.startActivity(uninstallIntent)

            // Remove from queue (Lifecycle owns it)
            uninstallQueue = uninstallQueue.drop(1)

            Timber.d("║ ✅ First app launched")
            Timber.d("║ Queue size after: ${uninstallQueue.size}")
            Timber.d("╚═══════════════════════════════════════════════════════")
        }
    }

    // Lifecycle observer - For ALL apps during batch uninstall
    DisposableEffect(lifecycleOwner, isUninstalling) {
        if (isUninstalling) {
            Timber.d("╔═══════════════════════════════════════════════════════")
            Timber.d("║ 📌 LIFECYCLE OBSERVER ATTACHED (Batch uninstall active)")
            Timber.d("╚═══════════════════════════════════════════════════════")

            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME && isUninstalling) {
                    Timber.d("╔═══════════════════════════════════════════════════════")
                    Timber.d("║ 🔄 LIFECYCLE: ON_RESUME during batch uninstall")
                    Timber.d("║ Lifecycle role: Launch next app OR show final results")

                    // Launch coroutine to push next app
                    coroutineScope.launch {
                        // Small delay just to ensure state is stable (not waiting for BroadcastReceiver)
                        delay(200)

                        // Re-check state after delay
                        if (!isUninstalling) {
                            Timber.d("║ ℹ️ Batch uninstall already completed")
                            Timber.d("╚═══════════════════════════════════════════════════════")
                            return@launch
                        }

                        Timber.d("║ Queue size: ${uninstallQueue.size}")
                        Timber.d("║ Queue: $uninstallQueue")

                        if (uninstallQueue.isNotEmpty()) {
                            // Always launch next app from queue (regardless of what happened to previous)
                            val nextPackage = uninstallQueue.first()
                            isLastAppInQueue = uninstallQueue.size == 1

                            Timber.d("║ 🚀 Lifecycle pushing next app: $nextPackage")
                            Timber.d("║ Remaining in queue: ${uninstallQueue.size}")
                            Timber.d("║ Is last app? $isLastAppInQueue")

                            val uninstallIntent = Intent(Intent.ACTION_DELETE).apply {
                                data = "package:$nextPackage".toUri()
                            }
                            context.startActivity(uninstallIntent)
                            Timber.d("║ ✅ Uninstall intent launched")

                            // Remove from queue after pushing (Lifecycle owns the queue)
                            uninstallQueue = uninstallQueue.drop(1)
                            Timber.d("║ Removed from queue - new size: ${uninstallQueue.size}")
                        } else {
                            // Queue is empty - all apps processed, do final verification
                            Timber.d("║ 🏁 All apps pushed - verifying final results")
                            Timber.d("║ Original queue: $originalQueueSnapshot")
                            Timber.d("║ Succeeded (tracked by BroadcastReceiver): $succeededPackages")
                            Timber.d("║ Succeeded count: $succeededCount")

                            // Find apps that were NOT in succeededPackages
                            val potentiallyFailed = originalQueueSnapshot.filter { it !in succeededPackages }
                            Timber.d("║ Potentially failed (not tracked): $potentiallyFailed")

                            // Verify what's actually still installed
                            val stillInstalled = mutableListOf<String>()
                            Timber.d("║ Querying PackageManager for final verification:")

                            potentiallyFailed.forEach { packageName ->
                                val isInstalled = try {
                                    pm.getApplicationInfo(packageName, 0)
                                    Timber.d("║   ❌ $packageName - Still installed (FAILED)")
                                    true
                                } catch (_: PackageManager.NameNotFoundException) {
                                    Timber.d("║   ✅ $packageName - Actually was uninstalled (BroadcastReceiver missed it)")
                                    false
                                }

                                if (isInstalled) {
                                    stillInstalled.add(packageName)
                                }
                            }

                            // Final counts based on PackageManager truth
                            failedPackages = stillInstalled
                            val actualSucceeded = originalQueueSnapshot.size - stillInstalled.size
                            succeededCount = actualSucceeded

                            Timber.d("║ Final verified counts:")
                            Timber.d("║   Total selected: ${originalQueueSnapshot.size}")
                            Timber.d("║   Succeeded (verified): $succeededCount")
                            Timber.d("║   Failed (verified): ${failedPackages.size}")
                            Timber.d("║   Failed packages: $failedPackages")

                            isUninstalling = false
                            isSelecting = false
                            selectedApps = emptySet()
                            isLastAppInQueue = false
                            succeededPackages = emptyList()

                            if (!showBatchResultDialog) {
                                Timber.d("║ 📊 Showing result dialog...")
                                showBatchResultDialog = true
                            }
                        }

                        Timber.d("╚═══════════════════════════════════════════════════════")
                    }
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                Timber.d("║ 📌 LIFECYCLE OBSERVER REMOVED")
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        } else {
            onDispose { }
        }
    }

    // Back handler
    BackHandler(enabled = true) {
        when {
            isSettingsVisible -> isSettingsVisible = false
            isUninstalling -> {
                // User pressed back during uninstall - mark remaining as failed
                Timber.d("╔═══════════════════════════════════════════════════════")
                Timber.d("║ ❌ USER PRESSED BACK DURING UNINSTALL")
                Timber.d("║ Marking remaining ${uninstallQueue.size} apps as failed")

                failedPackages = failedPackages + uninstallQueue
                uninstallQueue = emptyList()
                isUninstalling = false
                isSelecting = false
                selectedApps = emptySet()
                showBatchResultDialog = true

                Timber.d("║ Total succeeded: $succeededCount")
                Timber.d("║ Total failed: ${failedPackages.size}")
                Timber.d("╚═══════════════════════════════════════════════════════")
            }
            isSelecting -> {
                if (selectedApps.isNotEmpty()) {
                    showCancelConfirmationDialog = true
                } else {
                    isSelecting = false
                    selectedApps = emptySet()
                }
            }
            else -> showExitDialog = true
        }
    }

    // Main UI
    if (isSettingsVisible) {
        SettingsScreen(onBackClick = { isSettingsVisible = false })
    } else {
        Scaffold(
            topBar = {
                HomeTopBar(
                    onSettingsClick = { isSettingsVisible = true }
                )
            },
            bottomBar = {
                val total = filteredApps.size
                val allVisiblePackages = filteredApps.map { it.packageName }.toSet()
                val allSelected = selectedApps.containsAll(allVisiblePackages) && allVisiblePackages.isNotEmpty()

                SelectionBottomBar(
                    visible = isSelecting,
                    selectedCount = selectedApps.size,
                    totalCount = total,
                    allSelected = allSelected,
                    onToggleSelectAll = {
                        selectedApps = if (allSelected) {
                            selectedApps - allVisiblePackages
                        } else {
                            selectedApps + allVisiblePackages
                        }
                        if (!allSelected) isSelecting = true
                    },
                    onCancel = {
                        if (selectedApps.isNotEmpty()) {
                            showCancelConfirmationDialog = true
                        } else {
                            isSelecting = false
                            selectedApps = emptySet()
                        }
                    },
                    onUninstall = { showUninstallConfirm = true }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.padding(innerPadding)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Search Bar
                    SearchSection(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it }
                    )

                    // Filter Section
                    FilterSection(
                        selectedSortType = sortType,
                        sortAscending = sortAscending,
                        onSortTypeChange = { sortType = it },
                        onSortDirectionToggle = { sortAscending = !sortAscending }
                    )

                    // Content
                    when {
                        appListLoading -> LoadingState()
                        appListError != null -> {
                            val errorMsg = appListError ?: "Unknown error"
                            ErrorState(errorMessage = errorMsg)
                        }
                        filteredApps.isEmpty() && searchQuery.isNotEmpty() -> {
                            EmptySearchState(searchQuery = searchQuery)
                        }
                        filteredApps.isEmpty() -> NoAppsState()
                        else -> {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = filteredApps,
                                    key = { it.packageName }
                                ) { appInfo ->
                                    val isSelected = selectedApps.contains(appInfo.packageName)
                                    AppListItem(
                                        packageInfo = appInfo,
                                        isSelecting = isSelecting,
                                        isSelected = isSelected,
                                        onToggle = {
                                            selectedApps = if (isSelected) {
                                                selectedApps - appInfo.packageName
                                            } else {
                                                selectedApps + appInfo.packageName
                                            }
                                        },
                                        onStartSelection = {
                                            isSelecting = true
                                            selectedApps = setOf(appInfo.packageName)
                                        },
                                        onNormalClick = {
                                            detailsFor = appInfo
                                        },
                                        modifier = Modifier.animateItem()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialogs
        if (showExitDialog) {
            ExitConfirmationDialog(
                onCancel = { showExitDialog = false },
                onExit = { (context as? Activity)?.finish() }
            )
        }

        if (showCancelConfirmationDialog) {
            CancelConfirmationDialog(
                totalSelectedApps = selectedApps.size,
                onCancel = { showCancelConfirmationDialog = false },
                onExit = {
                    isSelecting = false
                    selectedApps = emptySet()
                    showCancelConfirmationDialog = false
                }
            )
        }

        if (showUninstallConfirm) {
            val selectedAppInfos = filteredApps.filter { selectedApps.contains(it.packageName) }
            ConfirmUninstallDialog(
                selectedPackages = selectedAppInfos,
                onDismiss = { showUninstallConfirm = false },
                onConfirmUninstall = {
                    showUninstallConfirm = false
                    if (selectedApps.isNotEmpty()) {
                        Timber.d("╔═══════════════════════════════════════════════════════")
                        Timber.d("║ 🚀 BATCH UNINSTALL STARTED")
                        Timber.d("║ Total apps selected: ${selectedApps.size}")
                        Timber.d("║ Selected packages: $selectedApps")

                        uninstallQueue = selectedApps.filter { packageName ->
                            try {
                                val appInfo = pm.getApplicationInfo(packageName, 0)
                                (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
                            } catch (_: PackageManager.NameNotFoundException) {
                                Timber.d("║ ⚠️ Package not found: $packageName")
                                false
                            }
                        }

                        Timber.d("║ Non-system apps to uninstall: ${uninstallQueue.size}")
                        Timber.d("║ Uninstall queue: $uninstallQueue")

                        // Save snapshot of original queue for final verification
                        originalQueueSnapshot = uninstallQueue
                        Timber.d("║ Original queue snapshot saved: $originalQueueSnapshot")

                        succeededPackages = emptyList()
                        failedPackages = emptyList()
                        succeededCount = 0
                        showBatchResultDialog = false

                        Timber.d("║ Tracking variables reset")
                        Timber.d("║ succeededCount: $succeededCount")
                        Timber.d("║ failedPackages: ${failedPackages.size}")

                        if (uninstallQueue.isNotEmpty()) {
                            isUninstalling = true
                            Timber.d("║ ✅ Batch uninstall mode activated")
                            Timber.d("║ Lifecycle observer will handle launching apps")
                        } else {
                            Timber.d("║ ❌ No apps to uninstall (all are system apps)")
                            viewModel.setError("Selected apps are system apps and cannot be uninstalled.")
                            isSelecting = false
                            selectedApps = emptySet()
                        }

                        Timber.d("╚═══════════════════════════════════════════════════════")
                    }
                }
            )
        }

        if (showBatchResultDialog) {
            Timber.d("╔═══════════════════════════════════════════════════════")
            Timber.d("║ 📊 SHOWING BATCH RESULT DIALOG")
            Timber.d("║ totalSelected: ${originalQueueSnapshot.size}")
            Timber.d("║ succeededCount: $succeededCount")
            Timber.d("║ failedPackages count: ${failedPackages.size}")
            Timber.d("║ failedPackages: $failedPackages")
            Timber.d("╚═══════════════════════════════════════════════════════")

            BatchUninstallResultDialog(
                totalSelected = originalQueueSnapshot.size,
                succeededCount = succeededCount,
                failedPackages = failedPackages,
                onDismiss = {
                    Timber.d("║ 🔚 Result dialog dismissed")
                    showBatchResultDialog = false
                    failedPackages = emptyList()
                    succeededCount = 0
                    originalQueueSnapshot = emptyList()
                }
            )
        }

        if (showFirstTutorial == true) {
            HowToUseDialog(onDismiss = { viewModel.setFirstLaunchDone() })
        }

        detailsFor?.let { pkg ->
            AppDetailsDialog(
                packageInfo = pkg,
                onDismiss = { detailsFor = null }
            )
        }
    }
}
