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

package com.rejowan.multiappuninstaller.presentation.settings.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.rejowan.multiappuninstaller.BuildConfig
import com.rejowan.multiappuninstaller.utils.MiscUtils

/**
 * About section for Settings
 * Follows QRCraft's Settings screen pattern
 * Contains app information, links, and bottom sheets
 *
 * @param modifier Optional modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutSection(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Bottom sheet states
    var showVersionSheet by remember { mutableStateOf(false) }
    var showPrivacySheet by remember { mutableStateOf(false) }
    var showLicensesSheet by remember { mutableStateOf(false) }
    var showCreatorSheet by remember { mutableStateOf(false) }
    var showAppLicenseSheet by remember { mutableStateOf(false) }

    // Section Header
    SectionHeader(title = "About", modifier = modifier)

    // Single About Card - All items in one card
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {
            // Version
            SettingsClickableItem(
                title = "Version ${BuildConfig.VERSION_NAME}",
                description = "View changelog",
                onClick = { showVersionSheet = true }
            )

            HorizontalDivider()

            // Privacy Policy
            SettingsClickableItem(
                title = "Privacy Policy",
                description = "View our privacy policy",
                onClick = { showPrivacySheet = true }
            )

            HorizontalDivider()

            // Open Source Licenses
            SettingsClickableItem(
                title = "Open Source Licenses",
                description = "View third-party libraries",
                onClick = { showLicensesSheet = true }
            )

            HorizontalDivider()

            // Creator
            SettingsClickableItem(
                title = "Creator",
                description = "About the developer",
                onClick = { showCreatorSheet = true }
            )

            HorizontalDivider()

            // App License
            SettingsClickableItem(
                title = "App License",
                description = "GNU General Public License v3.0",
                onClick = { showAppLicenseSheet = true }
            )

            HorizontalDivider()

            // Contact
            SettingsClickableItem(
                title = "Contact",
                description = "Get in touch with the developer",
                onClick = { MiscUtils().mailIntent(context) }
            )

            HorizontalDivider()

            // GitHub Repository
            SettingsClickableItem(
                title = "GitHub Repository",
                description = "View source code",
                onClick = {
                    openUrl(context, "https://github.com/ahmmedrejowan/MultiAppUninstaller")
                }
            )
        }
    }

    // Bottom Sheets
    if (showVersionSheet) {
        ModalBottomSheet(
            onDismissRequest = { showVersionSheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            ChangelogContent()
        }
    }

    if (showPrivacySheet) {
        ModalBottomSheet(
            onDismissRequest = { showPrivacySheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            PrivacyPolicyContent()
        }
    }

    if (showLicensesSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLicensesSheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            LicensesContent(context)
        }
    }

    if (showCreatorSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCreatorSheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            CreatorContent(context)
        }
    }

    if (showAppLicenseSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAppLicenseSheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            AppLicenseContent(context)
        }
    }
}

/**
 * Clickable settings item without icon (QRCraft pattern)
 */
@Composable
private fun SettingsClickableItem(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Changelog bottom sheet content
 */
@Composable
private fun ChangelogContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Changelog",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        ChangelogVersionItem(
            version = "0.2.0",
            date = "2025-11-21",
            changes = listOf(
                "Complete UI/UX redesign with Material 3",
                "Component-based architecture",
                "SegmentedButton for theme selection",
                "Modal bottom sheets for informational content",
                "Improved settings organization",
                "Updated to GPL-3.0 license",
                "Performance improvements and bug fixes"
            )
        )

        ChangelogVersionItem(
            version = "0.1.0",
            date = "2024-09-19",
            changes = listOf(
                "Initial release",
                "Batch app uninstall functionality",
                "Search and filter apps",
                "Sort by name, size, install date, and update date",
                "Material Design theme support",
                "Dynamic color support (Android 12+)"
            )
        )
    }
}

@Composable
private fun ChangelogVersionItem(
    version: String,
    date: String,
    changes: List<String>
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Version $version",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        changes.forEach { change ->
            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(text = "• ", style = MaterialTheme.typography.bodyMedium)
                Text(text = change, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/**
 * Privacy Policy bottom sheet content
 */
@Composable
private fun PrivacyPolicyContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Privacy Policy",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Privacy Highlights Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Your Privacy is Protected",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "✓ ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "No internet connection required",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "✓ ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "No data collection or tracking",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "✓ ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "No third-party services",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "✓ ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "All data stays on your device",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        PrivacySection(
            title = "No Data Collection",
            content = "Multi App Uninstaller does not collect, store, or transmit any personal data. The app operates entirely offline and does not require an internet connection."
        )

        PrivacySection(
            title = "Local Storage",
            content = "Your theme preferences and app settings are stored locally on your device using Android's shared preferences. This data never leaves your device."
        )

        PrivacySection(
            title = "No Analytics",
            content = "We do not use any analytics services, crash reporting tools, or third-party tracking mechanisms. Your usage of the app is completely private."
        )

        PrivacySection(
            title = "Permissions",
            content = "The app only requests necessary Android permissions to function: permission to query installed packages for displaying the app list."
        )
    }
}

@Composable
private fun PrivacySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Open Source Licenses bottom sheet content
 */
@Composable
private fun LicensesContent(context: Context) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Open Source Licenses",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LicenseItem(
            context = context,
            name = "Jetpack Compose",
            description = "Modern toolkit for building native Android UI",
            license = "Apache License 2.0",
            url = "https://github.com/androidx/androidx"
        )

        LicenseItem(
            context = context,
            name = "Material Components",
            description = "Material Design components for Android",
            license = "Apache License 2.0",
            url = "https://github.com/material-components/material-components-android"
        )

        LicenseItem(
            context = context,
            name = "Koin",
            description = "Pragmatic lightweight dependency injection",
            license = "Apache License 2.0",
            url = "https://github.com/InsertKoinIO/koin"
        )

        LicenseItem(
            context = context,
            name = "Timber",
            description = "A logger with a small, extensible API",
            license = "Apache License 2.0",
            url = "https://github.com/JakeWharton/timber"
        )

        LicenseItem(
            context = context,
            name = "Kotlin Coroutines",
            description = "Library support for Kotlin coroutines",
            license = "Apache License 2.0",
            url = "https://github.com/Kotlin/kotlinx.coroutines"
        )
    }
}

@Composable
private fun LicenseItem(
    context: Context,
    name: String,
    description: String,
    license: String,
    url: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable {
                openUrl(context, url)
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = license,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

/**
 * Creator bottom sheet content
 */
@Composable
private fun CreatorContent(context: Context) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "About the Creator",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "K M Rejowan Ahmmed",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Senior Android Developer",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Text(
            text = "As a developer who frequently manages multiple apps on Android devices, I created Multi App Uninstaller to simplify the process of cleaning up unnecessary applications. This app is designed to save time and make device management effortless.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                CreatorLinkItem(
                    icon = "🌐",
                    label = "Website",
                    value = "rejowan.com",
                    onClick = { openUrl(context, "https://rejowan.com") }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                CreatorLinkItem(
                    icon = "📧",
                    label = "Email",
                    value = "kmrejowan@gmail.com",
                    onClick = { MiscUtils().mailIntent(context) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                CreatorLinkItem(
                    icon = "💼",
                    label = "LinkedIn",
                    value = "@ahmmedrejowan",
                    onClick = { openUrl(context, "https://linkedin.com/in/ahmmedrejowan") }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                CreatorLinkItem(
                    icon = "🔗",
                    label = "GitHub",
                    value = "@ahmmedrejowan",
                    onClick = { openUrl(context, "https://github.com/ahmmedrejowan") }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Made with ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "❤️",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = " by ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "K M Rejowan Ahmmed",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CreatorLinkItem(
    icon: String,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(end = 16.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * App License (GPL-3.0) bottom sheet content
 */
@Composable
private fun AppLicenseContent(context: Context) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "GNU General Public License v3.0",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = """
Multi App Uninstaller
Copyright (C) 2025 K M Rejowan Ahmmed

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
            """.trimIndent(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Key Terms",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LicenseTermItem("✓ Freedom to use the software for any purpose")
                LicenseTermItem("✓ Freedom to study and modify the source code")
                LicenseTermItem("✓ Freedom to distribute copies to help others")
                LicenseTermItem("✓ Freedom to distribute modified versions")
                LicenseTermItem("✓ Copyleft: Derivative works must use GPL-3.0")
            }
        }

        Text(
            text = "This is a summary. For the complete license terms, please visit the official GPL v3.0 website.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        TextButton(
            onClick = {
                openUrl(context, "https://www.gnu.org/licenses/gpl-3.0.en.html")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "View Full GPL v3.0 License",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LicenseTermItem(text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * Helper function to open URL
 */
private fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}
