/*
 * Multi App Uninstaller
 * Copyright (C) 2025 K M Rejowan Ahmmed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.rejowan.multiappuninstaller.presentation.settings

import android.content.Intent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.InstallMobile
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.rejowan.multiappuninstaller.BuildConfig
import com.rejowan.multiappuninstaller.R
import com.rejowan.multiappuninstaller.data.ApkDownloadManager
import com.rejowan.multiappuninstaller.data.GithubRelease
import com.rejowan.multiappuninstaller.data.UpdateState
import com.rejowan.multiappuninstaller.feature.components.DownloadProgressSheet
import com.rejowan.multiappuninstaller.feature.components.UpdateAvailableSheet
import com.rejowan.multiappuninstaller.presentation.settings.components.ChangelogSheet
import com.rejowan.multiappuninstaller.presentation.settings.components.CreatorSheet
import com.rejowan.multiappuninstaller.presentation.settings.components.LicenseSheet
import com.rejowan.multiappuninstaller.presentation.settings.components.OpenSourceLicensesSheet
import com.rejowan.multiappuninstaller.presentation.settings.components.PrivacyPolicySheet
import com.rejowan.multiappuninstaller.presentation.settings.components.ThemePickerSheet
import com.rejowan.multiappuninstaller.presentation.settings.components.UpdateIntervalSheet
import com.rejowan.multiappuninstaller.ui.theme.AccentColors
import com.rejowan.multiappuninstaller.ui.theme.SoftAccents
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

private const val GITHUB_URL = "https://github.com/ahmmedrejowan/MultiAppUninstaller"

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val theme by viewModel.theme.collectAsState()
    val dynamicColorEnabled by viewModel.dynamicColorEnabled.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val downloadState by viewModel.downloadState.collectAsState()
    val updateCheckInterval by viewModel.updateCheckInterval.collectAsState()

    // Download sheet tracking
    var showDownloadSheet by remember { mutableStateOf(false) }
    var currentDownloadRelease by remember { mutableStateOf<GithubRelease?>(null) }

    // Sheet states
    var showThemeSheet by remember { mutableStateOf(false) }
    var showUpdateIntervalSheet by remember { mutableStateOf(false) }
    var showChangelogSheet by remember { mutableStateOf(false) }
    var showPrivacySheet by remember { mutableStateOf(false) }
    var showLicensesSheet by remember { mutableStateOf(false) }
    var showLicenseSheet by remember { mutableStateOf(false) }
    var showCreatorSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadTheme()
        viewModel.setDefaultThemeIfNotSet()
        viewModel.loadDynamicColorPreference()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header Card
            SettingsHeaderCard()

            Spacer(modifier = Modifier.height(24.dp))

            // APP SETTINGS
            SectionLabel(text = "APP SETTINGS", delay = 0)
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Palette,
                title = "Theme",
                subtitle = when (theme) {
                    "Light" -> "Light mode"
                    "Dark" -> "Dark mode"
                    else -> "System default"
                },
                accentColor = SoftAccents.Purple,
                onClick = { showThemeSheet = true },
                animationDelay = 50
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsToggleItem(
                icon = Icons.Rounded.ColorLens,
                title = "Dynamic Color",
                subtitle = "Use colors from your wallpaper",
                accentColor = SoftAccents.Teal,
                checked = dynamicColorEnabled,
                onCheckedChange = { viewModel.saveDynamicColorPreference(it) },
                animationDelay = 100
            )

            Spacer(modifier = Modifier.height(24.dp))

            // UPDATES
            SectionLabel(text = "UPDATES", delay = 150)
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.SystemUpdate,
                title = "Check for Updates",
                subtitle = when (updateState) {
                    is UpdateState.Idle -> "Tap to check for updates"
                    is UpdateState.Checking -> "Checking..."
                    is UpdateState.Available -> "Update available: v${(updateState as UpdateState.Available).release.version}"
                    is UpdateState.UpToDate -> "You're up to date"
                    is UpdateState.Error -> "Error: ${(updateState as UpdateState.Error).message}"
                },
                accentColor = SoftAccents.Amber,
                onClick = { viewModel.checkForUpdates() },
                animationDelay = 200
            )

            if (updateState is UpdateState.Available) {
                Spacer(modifier = Modifier.height(8.dp))
                val release = (updateState as UpdateState.Available).release
                SettingsOptionItem(
                    icon = Icons.Rounded.InstallMobile,
                    title = "Download Update",
                    subtitle = "v${release.version} is ready",
                    accentColor = AccentColors.Green,
                    onClick = {
                        // The UpdateAvailableSheet handles this — just let it show
                    },
                    animationDelay = 210
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Schedule,
                title = "Auto-check Interval",
                subtitle = updateCheckInterval.displayName,
                accentColor = SoftAccents.Blue,
                onClick = { showUpdateIntervalSheet = true },
                animationDelay = 250
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ABOUT
            SectionLabel(text = "ABOUT", delay = 300)
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Info,
                title = "Version ${BuildConfig.VERSION_NAME}",
                subtitle = "View changelog",
                accentColor = SoftAccents.Blue,
                onClick = { showChangelogSheet = true },
                animationDelay = 350
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Policy,
                title = "Privacy Policy",
                subtitle = "How we handle your data",
                accentColor = SoftAccents.Teal,
                onClick = { showPrivacySheet = true },
                animationDelay = 400
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Gavel,
                title = "Open Source Licenses",
                subtitle = "View third-party libraries",
                accentColor = SoftAccents.Purple,
                onClick = { showLicensesSheet = true },
                animationDelay = 450
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Person,
                title = "Creator",
                subtitle = "About the developer",
                accentColor = SoftAccents.Amber,
                onClick = { showCreatorSheet = true },
                animationDelay = 500
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Gavel,
                title = "App License",
                subtitle = "GPL-3.0 License",
                accentColor = SoftAccents.Blue,
                onClick = { showLicenseSheet = true },
                animationDelay = 550
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Email,
                title = "Contact",
                subtitle = "Get in touch",
                accentColor = SoftAccents.Teal,
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:kmrejowan@gmail.com".toUri()
                        putExtra(Intent.EXTRA_SUBJECT, "Multi App Uninstaller Feedback")
                    }
                    context.startActivity(intent)
                },
                animationDelay = 600
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Code,
                title = "Source Code",
                subtitle = "View on GitHub",
                accentColor = SoftAccents.Purple,
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, GITHUB_URL.toUri()))
                },
                animationDelay = 650
            )
        }
    }

    // Bottom Sheets
    if (showThemeSheet) {
        ThemePickerSheet(
            currentTheme = theme,
            onSelect = { viewModel.saveTheme(it); showThemeSheet = false },
            onDismiss = { showThemeSheet = false }
        )
    }

    if (showUpdateIntervalSheet) {
        UpdateIntervalSheet(
            currentInterval = updateCheckInterval,
            onSelect = { viewModel.setUpdateCheckInterval(it); showUpdateIntervalSheet = false },
            onDismiss = { showUpdateIntervalSheet = false }
        )
    }

    if (showChangelogSheet) { ChangelogSheet(onDismiss = { showChangelogSheet = false }) }
    if (showPrivacySheet) { PrivacyPolicySheet(onDismiss = { showPrivacySheet = false }) }
    if (showLicensesSheet) { OpenSourceLicensesSheet(onDismiss = { showLicensesSheet = false }) }
    if (showLicenseSheet) { LicenseSheet(onDismiss = { showLicenseSheet = false }) }
    if (showCreatorSheet) { CreatorSheet(onDismiss = { showCreatorSheet = false }) }

    // Update Available Sheet
    if (updateState is UpdateState.Available) {
        val availableState = updateState as UpdateState.Available
        UpdateAvailableSheet(
            release = availableState.release,
            currentVersion = availableState.currentVersion,
            onDismiss = { viewModel.dismissUpdateDialog() },
            onSkipVersion = {
                viewModel.skipVersion(availableState.release.version)
            },
            onDownload = {
                currentDownloadRelease = availableState.release
                showDownloadSheet = true
                viewModel.startDownload(availableState.release)
                viewModel.dismissUpdateDialog()
            }
        )
    }

    // Download Progress Sheet
    if (showDownloadSheet) {
        DownloadProgressSheet(
            downloadState = downloadState,
            versionName = currentDownloadRelease?.version ?: "",
            canInstall = viewModel.canInstallApks(),
            onDismiss = {
                showDownloadSheet = false
                if (downloadState !is ApkDownloadManager.DownloadState.Downloading &&
                    downloadState !is ApkDownloadManager.DownloadState.Starting) {
                    viewModel.resetDownloadState()
                }
            },
            onCancel = { viewModel.cancelDownload() },
            onInstall = { viewModel.installDownloadedApk() },
            onRequestPermission = {
                viewModel.openInstallPermissionSettings()?.let { intent ->
                    context.startActivity(intent)
                }
            }
        )
    }
}

// ============================================================================
// COMPOSABLE COMPONENTS
// ============================================================================

@Composable
private fun SectionLabel(text: String, delay: Int, modifier: Modifier = Modifier) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(delay.toLong()); isVisible = true }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.9f,
        animationSpec = tween(250, easing = FastOutSlowInEasing), label = "section scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(200), label = "section alpha"
    )

    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = MaterialTheme.typography.labelMedium.letterSpacing * 1.5f
        ),
        color = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
        modifier = modifier.scale(scale).padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsHeaderCard() {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.95f,
        animationSpec = tween(300, easing = FastOutSlowInEasing), label = "header scale"
    )

    Surface(
        modifier = Modifier.fillMaxWidth().scale(scale),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.img_splash_logo),
                    contentDescription = "App logo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Multi App Uninstaller", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Batch Uninstall Made Simple", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Version ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun SettingsOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit,
    animationDelay: Int,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(animationDelay.toLong()); isVisible = true }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.95f,
        animationSpec = tween(250, easing = FastOutSlowInEasing), label = "item scale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = accentColor),
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(10.dp), color = accentColor.copy(alpha = 0.12f)) {
                Icon(icon, contentDescription = null, modifier = Modifier.padding(8.dp).size(20.dp), tint = accentColor)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    animationDelay: Int,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(animationDelay.toLong()); isVisible = true }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.95f,
        animationSpec = tween(250, easing = FastOutSlowInEasing), label = "item scale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = accentColor),
                onClick = { onCheckedChange(!checked) }
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(10.dp), color = accentColor.copy(alpha = 0.12f)) {
                Icon(icon, contentDescription = null, modifier = Modifier.padding(8.dp).size(20.dp), tint = accentColor)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = accentColor,
                    checkedTrackColor = accentColor.copy(alpha = 0.3f)
                )
            )
        }
    }
}
