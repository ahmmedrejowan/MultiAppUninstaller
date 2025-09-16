package com.rejowan.multiappuninstaller.feature.module.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rejowan.multiappuninstaller.di.mainModule
import com.rejowan.multiappuninstaller.feature.components.SelectionBottomBar
import com.rejowan.multiappuninstaller.feature.module.home.component.AppTopBar
import com.rejowan.multiappuninstaller.feature.module.home.component.HomeContent
import com.rejowan.multiappuninstaller.receivers.AppUninstallReceiver
import com.rejowan.multiappuninstaller.utils.SortConfig
import com.rejowan.multiappuninstaller.utils.sortApps
import com.rejowan.multiappuninstaller.vm.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel = koinViewModel(),
) {

    val context = LocalContext.current
    val pm = context.packageManager

    val appList by mainViewModel.apps.collectAsState()
    val appListError by mainViewModel.error.collectAsState()
    val appListLoading by mainViewModel.loading.collectAsState()

    val hasPackagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.checkSelfPermission(Manifest.permission.QUERY_ALL_PACKAGES) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }


    var sortConfig by rememberSaveable { mutableStateOf(SortConfig()) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current


    var searchQuery by remember { mutableStateOf("") }
    var isSearch by remember { mutableStateOf(false) }

    var showExitBottomSheet by remember { mutableStateOf(false) }
    var showCancelConfirmationDialog by remember { mutableStateOf(false) }
    var showUninstallConfirm by remember { mutableStateOf(false) }


    var isSelecting by rememberSaveable { mutableStateOf(false) }

    var uninstallQueue by rememberSaveable(
        stateSaver = listSaver<List<String>, String>(save = { it }, restore = { it })
    ) { mutableStateOf(emptyList<String>()) }

    var isUninstalling by rememberSaveable { mutableStateOf(false) }


    val setSaver = listSaver<Set<String>, String>(save = { it.toList() }, restore = { it.toSet() })


    var selectedApps by rememberSaveable(stateSaver = setSaver) { mutableStateOf(emptySet<String>()) }


    val filteredApps by remember(appList, sortConfig, searchQuery) {
        derivedStateOf {
            sortApps(
                appList.filter { it.applicationInfo?.loadLabel(context.packageManager)?.contains(searchQuery, true) == true },
                pm,
                sortConfig
            )
        }
    }

    // --- Batch uninstall result tracking ---
    var totalSelectedAtStart by rememberSaveable { mutableStateOf(0) }

    val failedSaver = listSaver<List<String>, String>(save = { it }, restore = { it })
    var failedPackages by rememberSaveable(stateSaver = failedSaver) { mutableStateOf(emptyList<String>()) }
    var succeededCount by rememberSaveable { mutableStateOf(0) }

    var showBatchResultDialog by rememberSaveable { mutableStateOf(false) }


    val onAppUninstalled: (String) -> Unit = { packageName ->
        mainViewModel.removeAppByPackageName(packageName)

        if (uninstallQueue.isNotEmpty()) {

            succeededCount += 1


            // Remove the uninstalled app from the queue
            uninstallQueue = uninstallQueue.drop(1)
            // Trigger the next uninstall if the queue is not empty
            if (uninstallQueue.isNotEmpty()) {
                val nextPackage = uninstallQueue.first()
                val uninstallIntent = Intent(Intent.ACTION_DELETE).apply {
                    data = Uri.parse("package:$nextPackage")
                }
                context.startActivity(uninstallIntent)
            } else {
                // Uninstall process is complete
                isUninstalling = false
                isSelecting = false
                selectedApps = emptySet()
                showBatchResultDialog = true
            }
        }
    }

    val appUninstallReceiver = remember { AppUninstallReceiver(onAppUninstalled) }

    DisposableEffect(context) {
        val filter = IntentFilter(Intent.ACTION_PACKAGE_REMOVED)
        filter.addDataScheme("package")
        context.registerReceiver(appUninstallReceiver, filter)
        onDispose {
            context.unregisterReceiver(appUninstallReceiver)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && isUninstalling && uninstallQueue.isNotEmpty()) {
                // Check if the current app in the queue is still installed
                val currentPackage = uninstallQueue.first()
                val isAppStillInstalled = try {
                    pm.getApplicationInfo(currentPackage, 0)
                    true
                } catch (e: PackageManager.NameNotFoundException) {
                    false
                }

                if (isAppStillInstalled) {

                    failedPackages = failedPackages + currentPackage


                    // Remove the canceled app from the queue
                    uninstallQueue = uninstallQueue.drop(1)
                    // Proceed to the next app if the queue is not empty
                    if (uninstallQueue.isNotEmpty()) {
                        val nextPackage = uninstallQueue.first()
                        val uninstallIntent = Intent(Intent.ACTION_DELETE).apply {
                            data = Uri.parse("package:$nextPackage")
                        }
                        context.startActivity(uninstallIntent)
                    } else {
                        // Queue is empty, end the uninstall process
                        isUninstalling = false
                        isSelecting = false
                        selectedApps = emptySet()
                        showBatchResultDialog = true
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    if (!hasPackagePermission) {
        LaunchedEffect(Unit) { mainViewModel.setError("Permission not granted to access app list.") }
    } else {
        LaunchedEffect(Unit) { mainViewModel.loadApps() }
    }




    fun currentAllPackages(): Set<String> = filteredApps.map { it.packageName }.toSet()


    BackHandler(enabled = true) {
        when {
            isSearch -> {
                isSearch = false
                searchQuery = ""
                focusManager.clearFocus()
            }

            isSelecting -> {
                if (selectedApps.isNotEmpty()) {
                    showCancelConfirmationDialog = true
                } else {
                    isSelecting = false
                    selectedApps = emptySet()
                }
            }

            else -> showExitBottomSheet = true
        }
    }


    Scaffold(
        topBar = {
            AppTopBar(
                isSearch = isSearch,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onSearchToggle = {
                    isSearch = !isSearch
                    if (!isSearch) {
                        searchQuery = ""
                        focusManager.clearFocus()
                    }
                },
                onSettingsClick = { /* Handle settings click */ },
                focusRequester = focusRequester
            )
        },

        bottomBar = {
            val total = filteredApps.size
            val allVisiblePkgs = currentAllPackages()
            val allSelected = selectedApps.containsAll(allVisiblePkgs) && allVisiblePkgs.isNotEmpty()

            SelectionBottomBar(
                visible = isSelecting,
                selectedCount = selectedApps.size,
                totalCount = total,
                allSelected = allSelected,
                onToggleSelectAll = {
                    if (allSelected) {
                        selectedApps = selectedApps - allVisiblePkgs
                    } else {
                        selectedApps = selectedApps + allVisiblePkgs
                        isSelecting = true
                    }
                },
                onCancel = {
                    if (selectedApps.isNotEmpty()) {
                        showCancelConfirmationDialog = true
                    } else {
                        isSelecting = false
                        selectedApps = emptySet()
                    }

                },
                onUninstall = {
                    showUninstallConfirm = true
                })
        },

        containerColor = MaterialTheme.colorScheme.background,

        ) { innerPadding ->

        HomeContent(
            appListLoading = appListLoading,
            appListError = appListError,
            filteredApps = filteredApps,
            sortConfig = sortConfig,
            onSortConfigChange = { sortConfig = it },
            isSearch = isSearch,
            searchQuery = searchQuery,
            onSearchToggle = { isSearch = false },
            focusManager = focusManager,
            modifier = Modifier.padding(innerPadding),
            showExitBottomSheet = showExitBottomSheet,
            showCancelConfirmationDialog = showCancelConfirmationDialog,
            onDismissExitBottomSheet = { showExitBottomSheet = false },
            onExit = { (context as? Activity)?.finish() },
            isSelecting = isSelecting,
            selectedApps = selectedApps,
            onToggleSelection = { packageName ->
                selectedApps = if (selectedApps.contains(packageName)) {
                    selectedApps - packageName
                } else {
                    selectedApps + packageName
                }
            },
            onStartSelection = { packageName ->
                isSelecting = true
                selectedApps = setOf(packageName)
            },
            onDismissCancelConfirmationDialog = { showCancelConfirmationDialog = false },
            onExitCancelConfirmationDialog = {
                isSelecting = false
                selectedApps = emptySet()
                showCancelConfirmationDialog = false
            },
            showUninstallConfirmationDialog = showUninstallConfirm,
            onDismissUninstallConfirmationDialog = {
                showUninstallConfirm = false
            },
            onConfirmUninstall = {
                showUninstallConfirm = false
                if (selectedApps.isNotEmpty()) {
                    // Filter out system apps and initialize the uninstall queue
                    uninstallQueue = selectedApps.filter { packageName ->
                        try {
                            val appInfo = pm.getApplicationInfo(packageName, 0)
                            (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
                        } catch (e: PackageManager.NameNotFoundException) {
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
                            data = Uri.parse("package:$firstPackage")
                        }
                        context.startActivity(uninstallIntent)
                    } else {
                        mainViewModel.setError("Selected apps are system apps and cannot be uninstalled.")
                        isSelecting = false
                        selectedApps = emptySet()
                    }
                }
            },
            showBatchResultDialog = showBatchResultDialog,
            totalSelectedAtStart = totalSelectedAtStart,
            succeededCount = succeededCount,
            failedPackages = failedPackages,
            onDismissUninstallResultDialog = {
                showBatchResultDialog = false
                failedPackages = emptyList()
                totalSelectedAtStart = 0
                succeededCount = 0
            },
        )

    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val context = LocalContext.current
    KoinApplication(application = {
        androidContext(context)
        modules(listOf(mainModule))
    }) {
        HomeScreen()
    }
}