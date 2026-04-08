/*
 * Multi App Uninstaller
 * Copyright (C) 2025 K M Rejowan Ahmmed
 */

package com.rejowan.multiappuninstaller.feature.components

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.InstallMobile
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rejowan.multiappuninstaller.data.ApkDownloadManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadProgressSheet(
    downloadState: ApkDownloadManager.DownloadState,
    versionName: String,
    canInstall: Boolean,
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
    onInstall: () -> Unit,
    onRequestPermission: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (downloadState) {
                is ApkDownloadManager.DownloadState.Idle,
                is ApkDownloadManager.DownloadState.Starting -> {
                    DownloadingSection(0, 0, 0, versionName, isStarting = true, onCancel = onCancel)
                }
                is ApkDownloadManager.DownloadState.Downloading -> {
                    DownloadingSection(downloadState.progress, downloadState.downloadedBytes, downloadState.totalBytes, versionName, isStarting = false, onCancel = onCancel)
                }
                is ApkDownloadManager.DownloadState.Completed -> {
                    CompletedSection(versionName, canInstall, onInstall, onRequestPermission, onDismiss)
                }
                is ApkDownloadManager.DownloadState.Failed -> {
                    FailedSection(downloadState.reason, onDismiss)
                }
                is ApkDownloadManager.DownloadState.Cancelled -> {
                    CancelledSection(onDismiss)
                }
            }
        }
    }
}

@Composable
private fun DownloadingSection(progress: Int, downloadedBytes: Long, totalBytes: Long, versionName: String, isStarting: Boolean, onCancel: () -> Unit) {
    StatusIcon(Icons.Rounded.Download, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
    Spacer(modifier = Modifier.height(16.dp))
    Text(if (isStarting) "Starting Download..." else "Downloading Update", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Text("Version $versionName", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(modifier = Modifier.height(24.dp))

    if (isStarting) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp), strokeWidth = 4.dp, strokeCap = StrokeCap.Round)
    } else {
        val animatedProgress by animateFloatAsState(targetValue = progress / 100f, animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec, label = "progress")
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$progress%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(progress = { animatedProgress }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), strokeCap = StrokeCap.Round)
            Spacer(modifier = Modifier.height(8.dp))
            Text("${formatBytes(downloadedBytes)} / ${formatBytes(totalBytes)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceContainerLow) {
        Text("You can close this sheet. Download continues in the background.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.padding(12.dp))
    }
    Spacer(modifier = Modifier.height(20.dp))
    OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Icon(Icons.Rounded.Cancel, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Cancel Download")
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun CompletedSection(versionName: String, canInstall: Boolean, onInstall: () -> Unit, onRequestPermission: () -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var canInstallApks by remember { mutableStateOf(canInstall) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                canInstallApks = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.packageManager.canRequestPackageInstalls() else true
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    StatusIcon(Icons.Rounded.CheckCircle, Color(0xFF4CAF50).copy(alpha = 0.15f), Color(0xFF4CAF50))
    Spacer(modifier = Modifier.height(16.dp))
    Text("Download Complete", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Text("v$versionName is ready to install", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(modifier = Modifier.height(24.dp))

    if (canInstallApks) {
        Button(onClick = onInstall, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
            Icon(Icons.Rounded.InstallMobile, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Install Update")
        }
    } else {
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Rounded.Security, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(24.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Permission Required", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text("Allow installing apps from this source", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onRequestPermission, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Icon(Icons.Rounded.Security, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Grant Permission")
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
    OutlinedButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Later") }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun FailedSection(reason: String, onDismiss: () -> Unit) {
    StatusIcon(Icons.Rounded.Error, MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
    Spacer(modifier = Modifier.height(16.dp))
    Text("Download Failed", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)) {
        Text(reason, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
    }
    Spacer(modifier = Modifier.height(24.dp))
    Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Close") }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun CancelledSection(onDismiss: () -> Unit) {
    StatusIcon(Icons.Rounded.Cancel, MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(modifier = Modifier.height(16.dp))
    Text("Download Cancelled", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Text("You can try again from Settings", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(modifier = Modifier.height(24.dp))
    Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Close") }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun StatusIcon(icon: ImageVector, containerColor: Color, iconColor: Color) {
    Surface(shape = CircleShape, color = containerColor, modifier = Modifier.size(72.dp)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(36.dp), tint = iconColor)
        }
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    return when {
        mb >= 1.0 -> String.format("%.1f MB", mb)
        kb >= 1.0 -> String.format("%.1f KB", kb)
        else -> "$bytes B"
    }
}
