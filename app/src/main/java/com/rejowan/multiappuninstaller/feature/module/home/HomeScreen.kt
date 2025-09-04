package com.rejowan.multiappuninstaller.feature.module.home

import COutlinedTextField
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rejowan.multiappuninstaller.di.mainModule
import com.rejowan.multiappuninstaller.feature.components.SingleAppInfoScreen
import com.rejowan.multiappuninstaller.feature.components.SortBar
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


    val filteredApps by remember(appList, sortConfig, searchQuery) {
        derivedStateOf {
            sortApps(
                appList.filter { it.applicationInfo?.loadLabel(context.packageManager)?.contains(searchQuery, true) == true },
                pm,
                sortConfig
            )
        }
    }


    Scaffold(
        topBar = {

            Crossfade(
                modifier = Modifier.animateContentSize(), targetState = isSearch, label = "Search"
            ) { target ->
                if (!target) {

                    Row(
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .height(80.dp)
                            .fillMaxWidth()
                            .windowInsetsPadding(TopAppBarDefaults.windowInsets),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        Text(
                            text = "Multi App Uninstaller",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                            modifier = Modifier.padding(start = 16.dp)
                        )

                        Box(modifier = Modifier.weight(1f))

                        IconButton(onClick = {
                            isSearch = !isSearch
                        }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }

                    }

                } else {


                    COutlinedTextField(
                        value = searchQuery,
                        onValueChange = { newValue ->
                            searchQuery = newValue
                        },
                        hint = "Search apps...",
                        modifier = Modifier
                            .padding(vertical = 6.dp, horizontal = 8.dp)
                            .fillMaxWidth()
                            .height(72.dp)
                            .focusRequester(focusRequester)
                            .windowInsetsPadding(TopAppBarDefaults.windowInsets),
                        maxLines = 1,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary),
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        leadingIcon = {
                            IconButton(onClick = {
                                isSearch = !isSearch
                                searchQuery = ""
                                focusManager.clearFocus()
                            }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                                )
                            }

                        },
                        trailingIcon = if (searchQuery.isNotBlank()) {
                            {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear")
                                }
                            }
                        } else {
                            null
                        }

                    )

                    LaunchedEffect(isSearch) {
                        if (isSearch) {
                            focusRequester.requestFocus()
                        }
                    }


                }
            }
        },

        containerColor = MaterialTheme.colorScheme.background,

        ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Column(modifier = Modifier.fillMaxSize()) {


                when {
                    appListLoading -> {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }

                    appListError != null -> {
                        Text(
                            text = appListError.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    else -> {
                        LazyColumn {
                            item {
                                SortBar(
                                    sortConfig = sortConfig,
                                    onChange = { sortConfig = it },
                                )
                            }

                            item {
                                if (filteredApps.isEmpty()) {

                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = "No apps found",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier
                                                .padding(16.dp)
                                                .align(Alignment.Center)
                                        )
                                    }

                                }
                            }


                            items(
                                items = filteredApps, key = { it.packageName }) { appInfo ->
                                SingleAppInfoScreen(packageInfo = appInfo)
                            }


                        }
                    }
                }
            }

        }

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