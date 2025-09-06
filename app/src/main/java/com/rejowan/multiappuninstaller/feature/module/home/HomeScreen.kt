package com.rejowan.multiappuninstaller.feature.module.home

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.rejowan.multiappuninstaller.di.mainModule
import com.rejowan.multiappuninstaller.feature.module.home.component.AppTopBar
import com.rejowan.multiappuninstaller.feature.module.home.component.HomeContent
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

    val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.checkSelfPermission(Manifest.permission.QUERY_ALL_PACKAGES) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

    if (!hasPermission) {
        LaunchedEffect(Unit) { mainViewModel.setError("Permission not granted to access app list.") }
    } else {
        LaunchedEffect(Unit) { mainViewModel.loadApps() }
    }

    var sortConfig by rememberSaveable { mutableStateOf(SortConfig()) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current


    var searchQuery by remember { mutableStateOf("") }
    var isSearch by remember { mutableStateOf(false) }

    var showExitBottomSheet by remember { mutableStateOf(false) }

    var isSelecting by rememberSaveable { mutableStateOf(false) }

    val SetSaver = listSaver<Set<String>, String>(save = { it.toList() }, restore = { it.toSet() })
    var selectedApps by rememberSaveable(stateSaver = SetSaver) { mutableStateOf(emptySet<String>()) }

    fun startSelection(packageName: String) {
        isSelecting = true
        selectedApps = setOf(packageName)
    }

    fun toggleSelection(packageName: String) {
        val next = selectedApps.toMutableSet().apply {
            if (contains(packageName)) remove(packageName) else add(packageName)
        }.toSet()

        selectedApps = next
        if (next.isEmpty()) {
            // auto-exit when nothing is selected
            isSelecting = false
        }
    }


    val filteredApps by remember(appList, sortConfig, searchQuery) {
        derivedStateOf {
            sortApps(
                appList.filter { it.applicationInfo?.loadLabel(context.packageManager)?.contains(searchQuery, true) == true },
                pm,
                sortConfig
            )
        }
    }

//    BackHandler(enabled = true) {
//        if (isSearch && searchQuery.isEmpty()) {
//            isSearch = false
//            searchQuery = ""
//            focusManager.clearFocus()
//            return@BackHandler
//        }
//
//        if (!isSearch) {
//            showExitBottomSheet = true
//        }
//
//    }
    BackHandler(enabled = true) {
        when {
            isSelecting -> {
                isSelecting = false
                selectedApps = emptySet()
            }

            isSearch -> {
                isSearch = false
                searchQuery = ""
                focusManager.clearFocus()
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
                if (selectedApps.isEmpty()) {
                    isSelecting = false
                }
            },
            onStartSelection = { packageName ->
                isSelecting = true
                selectedApps = setOf(packageName)
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