@file:OptIn(ExperimentalMaterial3Api::class)

package com.rejowan.multiappuninstaller.feature.module.setting

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.InvertColors
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.rejowan.multiappuninstaller.feature.components.CreatorDialog
import com.rejowan.multiappuninstaller.feature.components.CreditsDialog
import com.rejowan.multiappuninstaller.feature.components.HowToUseDialog
import com.rejowan.multiappuninstaller.feature.components.LicenseDialog
import com.rejowan.multiappuninstaller.feature.components.PrivacyDialog
import com.rejowan.multiappuninstaller.feature.components.VersionLogDialog
import com.rejowan.multiappuninstaller.ui.theme.MiscUtils
import com.rejowan.multiappuninstaller.vm.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    mainViewModel: MainViewModel = koinViewModel(), onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val theme by mainViewModel.theme.collectAsState()
    val dynamicColor = mainViewModel.dynamicColorEnabled.collectAsState()
    var showThemeDropdown by remember { mutableStateOf(false) }
    var showDynamicColorDropdown by remember { mutableStateOf(false) }
    var showHowToUseDialog by remember { mutableStateOf(false) }
    var showCreditsDialog by remember { mutableStateOf(false) }
    var showLicenseDialog by remember { mutableStateOf(false) }
    var showVersionDialog by remember { mutableStateOf(false) }
    var showCreatorDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        mainViewModel.loadTheme()
        mainViewModel.setDefaultThemeIfNotSet()
        mainViewModel.loadDynamicColorPreference()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }, navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back"
                    )
                }
            })
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Appearance", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp)
            )
            SettingsItem(
                icon = Icons.Outlined.Palette,
                title = "Theme",
                subtitle = "Your preferred mode: $theme",
                onClick = { showThemeDropdown = true })
            Box {
                DropdownMenu(
                    expanded = showThemeDropdown, onDismissRequest = { showThemeDropdown = false }) {
                    listOf("System Default", "Light", "Dark").forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = {
                            mainViewModel.saveTheme(option)
                            showThemeDropdown = false
                        })
                    }
                }
            }

            SettingsItem(
                icon = Icons.Outlined.InvertColors,
                title = "Color",
                subtitle = "Use Dynamic Color: ${if (dynamicColor.value) "Enabled" else "Disabled"}",
                onClick = { showDynamicColorDropdown = true })
            Box {
                DropdownMenu(
                    expanded = showDynamicColorDropdown, onDismissRequest = { showDynamicColorDropdown = false }) {
                    listOf(true, false).forEach { option ->
                        DropdownMenuItem(text = { Text(if (option) "Enable" else "Disable") }, onClick = {
                            mainViewModel.saveDynamicColorPreference(option)
                            showDynamicColorDropdown = false
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "About", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp)
            )
            SettingsItem(
                icon = Icons.Outlined.Person, title = "Created By", subtitle = "K M Rejowan Ahmmed (@ahmmedrejowan)", onClick = {
                    showCreatorDialog = true
                })
            SettingsItem(
                icon = Icons.Outlined.Info, title = "Version", subtitle = "0.1.0 (Build 1)", onClick = {
                    showVersionDialog = true
                })
            SettingsItem(
                icon = Icons.AutoMirrored.Outlined.HelpOutline,
                title = "How to Use",
                subtitle = "Quick guide on using the app",
                onClick = { showHowToUseDialog = true })
            SettingsItem(
                icon = Icons.Outlined.StarOutline,
                title = "Credits",
                subtitle = "Thanks to contributors and libraries used",
                onClick = { showCreditsDialog = true })
            SettingsItem(
                icon = Icons.Outlined.Code, title = "Source Code", subtitle = "View the open-source repository", onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://github.com/ahmmedrejowan/MultiAppUninstaller".toUri())
                    context.startActivity(intent)
                })
            SettingsItem(
                icon = Icons.Outlined.Gavel,
                title = "License",
                subtitle = "Apache License 2.0",
                onClick = { showLicenseDialog = true })
            SettingsItem(
                icon = Icons.Outlined.Security, title = "Privacy Policy", subtitle = "Read our privacy practices", onClick = {
                    showPrivacyDialog = true
                })
            SettingsItem(
                icon = Icons.Outlined.MailOutline, title = "Feedback", subtitle = "Send suggestions or report issues", onClick = {
                    MiscUtils().mailIntent(context)
                })

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                ),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://github.com/ahmmedrejowan/MultiAppUninstaller".toUri())
                    context.startActivity(intent)
                }) {

                Text(
                    text = "Check for Updates", style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp
                    )
                )

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Text(
                    text = "Multi App Uninstaller - 2025",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showHowToUseDialog) {
        HowToUseDialog(onDismiss = {
            showHowToUseDialog = false
        })
    }

    if (showVersionDialog) {
        VersionLogDialog {
            showVersionDialog = false
        }
    }

    if (showCreatorDialog) {
        CreatorDialog(onDismiss = {
            showCreatorDialog = false
        })
    }

    if (showCreditsDialog) {
        CreditsDialog(onDismiss = {
            showCreditsDialog = false
        })
    }


    if (showLicenseDialog) {
        LicenseDialog {
            showLicenseDialog = false
        }
    }

    if (showPrivacyDialog) {
        PrivacyDialog {
            showPrivacyDialog = false
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


