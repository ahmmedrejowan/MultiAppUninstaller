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
import com.rejowan.multiappuninstaller.presentation.settings.NewSettingsScreen
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
fun NewHomeScreen(
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
    var totalSelectedAtStart by rememberSaveable { mutableIntStateOf(0) }
    var succeededCount by rememberSaveable { mutableIntStateOf(0) }
    val failedSaver = listSaver<List<String>, String>(save = { it }, restore = { it })
    var failedPackages by rememberSaveable(stateSaver = failedSaver) { mutableStateOf(emptyList()) }

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
        if (!isSelecting) {
            viewModel.removeAppByPackageName(packageName)
        } else {
            succeededCount += 1
            uninstallQueue = uninstallQueue.drop(1)
            viewModel.removeAppByPackageName(packageName)
            if (uninstallQueue.isNotEmpty()) {
                val nextPackage = uninstallQueue.first()
                val uninstallIntent = Intent(Intent.ACTION_DELETE).apply {
                    data = "package:$nextPackage".toUri()
                }
                context.startActivity(uninstallIntent)
            } else {
                isUninstalling = false
                isSelecting = false
                selectedApps = emptySet()
                if (!showBatchResultDialog) {
                    showBatchResultDialog = true
                }
            }
        }
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

    // Lifecycle observer for handling user cancellation
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && isUninstalling && uninstallQueue.isNotEmpty()) {
                val currentPackage = uninstallQueue.first()
                val isAppStillInstalled = try {
                    pm.getApplicationInfo(currentPackage, 0)
                    true
                } catch (_: PackageManager.NameNotFoundException) {
                    false
                }
                if (isAppStillInstalled) {
                    failedPackages = failedPackages + currentPackage
                    uninstallQueue = uninstallQueue.drop(1)
                    if (uninstallQueue.isNotEmpty()) {
                        val nextPackage = uninstallQueue.first()
                        val uninstallIntent = Intent(Intent.ACTION_DELETE).apply {
                            data = "package:$nextPackage".toUri()
                        }
                        context.startActivity(uninstallIntent)
                    } else {
                        isUninstalling = false
                        isSelecting = false
                        selectedApps = emptySet()
                        if (!showBatchResultDialog) {
                            showBatchResultDialog = true
                        }
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
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

    // Back handler
    BackHandler(enabled = true) {
        when {
            isSettingsVisible -> isSettingsVisible = false
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
        NewSettingsScreen(onBackClick = { isSettingsVisible = false })
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
                        uninstallQueue = selectedApps.filter { packageName ->
                            try {
                                val appInfo = pm.getApplicationInfo(packageName, 0)
                                (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
                            } catch (_: PackageManager.NameNotFoundException) {
                                Timber.d("Package not found: $packageName")
                                false
                            }
                        }

                        totalSelectedAtStart = uninstallQueue.size
                        failedPackages = emptyList()
                        succeededCount = 0
                        showBatchResultDialog = false

                        if (uninstallQueue.isNotEmpty()) {
                            isUninstalling = true
                            val firstPackage = uninstallQueue.first()
                            val uninstallIntent = Intent(Intent.ACTION_DELETE).apply {
                                data = "package:$firstPackage".toUri()
                            }
                            context.startActivity(uninstallIntent)
                        } else {
                            viewModel.setError("Selected apps are system apps and cannot be uninstalled.")
                            isSelecting = false
                            selectedApps = emptySet()
                        }
                    }
                }
            )
        }

        if (showBatchResultDialog) {
            BatchUninstallResultDialog(
                totalSelected = totalSelectedAtStart,
                succeededCount = succeededCount,
                failedPackages = failedPackages,
                onDismiss = {
                    showBatchResultDialog = false
                    failedPackages = emptyList()
                    totalSelectedAtStart = 0
                    succeededCount = 0
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
