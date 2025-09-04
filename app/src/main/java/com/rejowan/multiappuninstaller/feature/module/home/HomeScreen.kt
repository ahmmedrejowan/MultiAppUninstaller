package com.rejowan.multiappuninstaller.feature.module.home

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rejowan.multiappuninstaller.di.mainModule
import com.rejowan.multiappuninstaller.feature.components.SingleAppInfoScreen
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
    val appList by mainViewModel.apps.collectAsState()
    val appListError by mainViewModel.error.collectAsState()
    val appListLoading by mainViewModel.loading.collectAsState()

    val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.checkSelfPermission(android.Manifest.permission.QUERY_ALL_PACKAGES) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

    if (!hasPermission) {
        mainViewModel.setError("Permission not granted to access app list.")
    } else {
        LaunchedEffect(Unit) {
            mainViewModel.loadApps()
        }
    }

    var expanded by remember { mutableStateOf(false) }
    var selectedSortingOption by remember { mutableStateOf("Name ASC") }

    // Sort logic
    val sortOptions = listOf("Name ASC", "Name DESC", "Size ASC", "Size DESC", "Date ASC", "Date DESC")
    val sortedApps = remember(appList, selectedSortingOption) {
        when (selectedSortingOption) {
            "Name ASC" -> appList.sortedBy { it.applicationInfo?.loadLabel(context.packageManager).toString().lowercase() }
            "Name DESC" -> appList.sortedByDescending {
                it.applicationInfo?.loadLabel(context.packageManager).toString().lowercase()
            }

            "Size ASC" -> appList.sortedBy { java.io.File(it.applicationInfo?.sourceDir).length() }
            "Size DESC" -> appList.sortedByDescending { java.io.File(it.applicationInfo?.sourceDir).length() }
            "Date ASC" -> appList.sortedBy { it.firstInstallTime }
            "Date DESC" -> appList.sortedByDescending { it.firstInstallTime }
            else -> appList
        }
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

                // Dropdown menu
                ExposedDropdownMenuBox(
                    expanded = expanded, onExpandedChange = { expanded = it }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Use OutlinedTextField as the anchor
                    androidx.compose.material3.OutlinedTextField(
                        value = selectedSortingOption,
                        onValueChange = { }, // Read-only, no direct input
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(), // Required to anchor the dropdown
                        readOnly = true, // Prevent keyboard input
                        label = { Text("Sort By") },
                        trailingIcon = {
                            androidx.compose.material3.Icon(
                                imageVector = if (expanded) {
                                    androidx.compose.material.icons.Icons.Default.ArrowDropUp
                                } else {
                                    androidx.compose.material.icons.Icons.Default.ArrowDropDown
                                }, contentDescription = null
                            )
                        })

                    ExposedDropdownMenu(
                        expanded = expanded, onDismissRequest = { expanded = false }) {
                        sortOptions.forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = {
                                selectedSortingOption = option
                                expanded = false
                            })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (appListLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else if (appListError != null) {
                    Text(
                        text = appListError.orEmpty(), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn {
                        items(sortedApps) { appInfo ->
                            SingleAppInfoScreen(
                                packageInfo = appInfo
                            )
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