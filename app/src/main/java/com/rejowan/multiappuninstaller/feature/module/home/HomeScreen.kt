package com.rejowan.multiappuninstaller.feature.module.home

import android.content.pm.PackageInfo
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rejowan.multiappuninstaller.feature.components.SingleAppInfoScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    val context = LocalContext.current
    var apps by remember { mutableStateOf<List<PackageInfo>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }


    var expanded by remember { mutableStateOf(false) }
    var selectedSortingOption by remember { mutableStateOf("Name ASC") }

    // Sort logic
    val sortOptions = listOf("Name ASC", "Name DESC", "Size ASC", "Size DESC", "Date ASC", "Date DESC")
    val sortedApps = remember(apps, selectedSortingOption) {
        when (selectedSortingOption) {
            "Name ASC" -> apps.sortedBy { it.applicationInfo?.loadLabel(context.packageManager).toString().lowercase() }
            "Name DESC" -> apps.sortedByDescending {
                it.applicationInfo?.loadLabel(context.packageManager).toString().lowercase()
            }

            "Size ASC" -> apps.sortedBy { java.io.File(it.applicationInfo?.sourceDir).length() }
            "Size DESC" -> apps.sortedByDescending { java.io.File(it.applicationInfo?.sourceDir).length() }
            "Date ASC" -> apps.sortedBy { it.firstInstallTime }
            "Date DESC" -> apps.sortedByDescending { it.firstInstallTime }
            else -> apps
        }
    }


    // Check and request permission if necessary (if using QUERY_ALL_PACKAGES permission)
    val hasPermission =
        context.checkSelfPermission(android.Manifest.permission.QUERY_ALL_PACKAGES) == PackageManager.PERMISSION_GRANTED
    if (!hasPermission) {
        error = "Permission not granted to access app list."
    }

    LaunchedEffect(Unit) {
        if (hasPermission) {
            loading = true
            try {
                // Fetch installed apps
                val pm = context.packageManager
                val packageList: List<PackageInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
                } else {
                    @Suppress("DEPRECATION") pm.getInstalledPackages(0)
                }

                // all apps
                //  apps = packageList.sortedBy { it.applicationInfo?.loadLabel(pm).toString().lowercase() }

                // user installed apps only
                apps = packageList.filter {
                    (it.applicationInfo?.flags?.and(android.content.pm.ApplicationInfo.FLAG_SYSTEM) ?: 0) == 0
                }.sortedBy { it.applicationInfo?.loadLabel(pm).toString().lowercase() }


                loading = false
            } catch (e: Exception) {
                error = "Failed to load apps: ${e.message}"
                loading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Multi App Uninstaller")
                })
        }

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

                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else if (error != null) {
                    Text(
                        text = error.orEmpty(), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp)
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
    HomeScreen()
}