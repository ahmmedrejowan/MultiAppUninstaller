package com.rejowan.multiappuninstaller.feature.module.home

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        context.checkSelfPermission(android.Manifest.permission.QUERY_ALL_PACKAGES) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

    if (!hasPermission) {
        LaunchedEffect(Unit) { mainViewModel.setError("Permission not granted to access app list.") }
    } else {
        LaunchedEffect(Unit) { mainViewModel.loadApps() }
    }

    var sortConfig by rememberSaveable { mutableStateOf(SortConfig()) }

    val sortedApps by remember(appList, sortConfig) {
        derivedStateOf { sortApps(appList, pm, sortConfig) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Multi App Uninstaller")
                })
        }, containerColor = MaterialTheme.colorScheme.background

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

                            items(
                                items = sortedApps, key = { it.packageName }) { appInfo ->
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