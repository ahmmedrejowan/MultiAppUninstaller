package com.rejowan.multiappuninstaller.presentation.settings.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.rejowan.multiappuninstaller.utils.MiscUtils

/**
 * About section for Settings
 * Contains app information, links, and dialogs
 *
 * @param modifier Optional modifier
 */
@Composable
fun AboutSection(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Dialog states
    var showHowToUseDialog by remember { mutableStateOf(false) }
    var showCreditsDialog by remember { mutableStateOf(false) }
    var showLicenseDialog by remember { mutableStateOf(false) }
    var showVersionDialog by remember { mutableStateOf(false) }
    var showCreatorDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    // Section Header
    SectionHeader(title = "About", modifier = modifier)

    // Creator
    SettingItem(
        icon = Icons.Outlined.Person,
        title = "Created By",
        subtitle = "K M Rejowan Ahmmed (@ahmmedrejowan)",
        onClick = { showCreatorDialog = true }
    )

    // Version
    SettingItem(
        icon = Icons.Outlined.Info,
        title = "Version",
        subtitle = "2.0.0 (Build 2)",
        onClick = { showVersionDialog = true }
    )

    // How to Use
    SettingItem(
        icon = Icons.AutoMirrored.Outlined.HelpOutline,
        title = "How to Use",
        subtitle = "Quick guide on using the app",
        onClick = { showHowToUseDialog = true }
    )

    // Credits
    SettingItem(
        icon = Icons.Outlined.StarOutline,
        title = "Credits",
        subtitle = "Thanks to contributors and libraries used",
        onClick = { showCreditsDialog = true }
    )

    // Source Code
    SettingItem(
        icon = Icons.Outlined.Code,
        title = "Source Code",
        subtitle = "View the open-source repository",
        onClick = {
            openUrl(context, "https://github.com/ahmmedrejowan/MultiAppUninstaller")
        }
    )

    // License
    SettingItem(
        icon = Icons.Outlined.Gavel,
        title = "License",
        subtitle = "GPL-3.0",
        onClick = { showLicenseDialog = true }
    )

    // Privacy Policy
    SettingItem(
        icon = Icons.Outlined.Security,
        title = "Privacy Policy",
        subtitle = "Read our privacy practices",
        onClick = { showPrivacyDialog = true }
    )

    // Feedback
    SettingItem(
        icon = Icons.Outlined.MailOutline,
        title = "Feedback",
        subtitle = "Send suggestions or report issues",
        onClick = { MiscUtils().mailIntent(context) }
    )

    // Check for Updates Button
    OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        ),
        onClick = {
            openUrl(context, "https://github.com/ahmmedrejowan/MultiAppUninstaller")
        }
    ) {
        Text(
            text = "Check for Updates",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp)
        )
    }

    // Footer
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Multi App Uninstaller - 2025",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Dialogs
    if (showHowToUseDialog) {
        HowToUseDialog(onDismiss = { showHowToUseDialog = false })
    }

    if (showVersionDialog) {
        VersionLogDialog { showVersionDialog = false }
    }

    if (showCreatorDialog) {
        CreatorDialog(onDismiss = { showCreatorDialog = false })
    }

    if (showCreditsDialog) {
        CreditsDialog(onDismiss = { showCreditsDialog = false })
    }

    if (showLicenseDialog) {
        LicenseDialog { showLicenseDialog = false }
    }

    if (showPrivacyDialog) {
        PrivacyDialog { showPrivacyDialog = false }
    }
}

/**
 * Helper function to open URL
 */
private fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}
