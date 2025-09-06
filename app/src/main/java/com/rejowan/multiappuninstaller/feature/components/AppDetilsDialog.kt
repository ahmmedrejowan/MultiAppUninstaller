package com.rejowan.multiappuninstaller.feature.components

import android.content.pm.PackageInfo
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.rejowan.multiappuninstaller.utils.DateFormatUtils
import java.io.File

@Composable
fun AppDetailsDialog(
    packageInfo: PackageInfo, onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val appIcon = packageInfo.applicationInfo?.loadIcon(context.packageManager)
    val appName = packageInfo.applicationInfo?.loadLabel(context.packageManager)?.toString() ?: "App Name"

    val sizeStr = packageInfo.applicationInfo?.sourceDir?.let { src ->
        val mb = File(src).length() / (1024f * 1024f)
        String.format("%.2f MB", mb)
    } ?: "Unknown size"

    val installDate = DateFormatUtils.millisToDateTime(packageInfo.firstInstallTime)
    val updateDate = DateFormatUtils.millisToDateTime(packageInfo.lastUpdateTime)
    val packageName = packageInfo.packageName
    val versionName = packageInfo.versionName ?: "Unknown"
    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode.toString()
    } else {
        @Suppress("DEPRECATION") packageInfo.versionCode.toString()
    }
    val minSdk = packageInfo.applicationInfo?.minSdkVersion?.toString() ?: "Unknown"
    val targetSdk = packageInfo.applicationInfo?.targetSdkVersion?.toString() ?: "Unknown"
    val installer = runCatching {
        context.packageManager.getInstallerPackageName(packageName)
    }.getOrNull() ?: "Unknown"

    AlertDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = onDismiss) { Text("Close") }
    }, text = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Centered icon
            appIcon?.let {
                Image(
                    painter = BitmapPainter(it.toBitmap().asImageBitmap()),
                    contentDescription = appName,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(top = 4.dp, bottom = 8.dp)
                )
            }

            // Centered app name
            Text(
                text = appName,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Left-aligned details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                InfoBlock(label = "Package Name", value = packageName)
                InfoBlock(label = "Version Info", value = "$versionName ($versionCode)")
                InfoBlock(label = "App Size", value = sizeStr)
                InfoBlock(label = "First Installed", value = installDate)
                InfoBlock(label = "Last Updated", value = updateDate)
                InfoBlock(label = "Min SDK", value = minSdk)
                InfoBlock(label = "Target SDK", value = targetSdk)
                InfoBlock(label = "Installer Package", value = installer)
            }
        }
    })
}

@Composable
private fun InfoBlock(
    label: String, value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface
        )
    }
}
