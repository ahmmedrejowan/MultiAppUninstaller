package com.rejowan.multiappuninstaller.feature.components

import android.content.pm.PackageManager
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.PlaylistAddCheckCircle
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap

/**
 * Batch Uninstall Result Dialog
 *
 * @param totalSelected total apps user attempted to uninstall
 * @param succeededCount number of apps actually uninstalled
 * @param failedPackages list of package names that failed/cancelled
 * @param onDismiss close dialog callback
 * @param onRetryFailed optional retry action for failed list (shows button if provided and there are failures)
 */
@Composable
fun BatchUninstallResultDialog(
    totalSelected: Int,
    succeededCount: Int,
    failedPackages: List<String>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val pm: PackageManager = context.packageManager
    val failedCount = failedPackages.size

    val titleText = when {
        totalSelected == 0 -> "Nothing Uninstalled"
        failedCount == 0 -> "All Uninstalled Successfully"
        else -> "Uninstall Summary"
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = titleText,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        },

        text = {
            Column(Modifier.fillMaxWidth()) {


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlaylistAddCheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Total Selected: $totalSelected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(8.dp))


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Uninstalled: $succeededCount",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(8.dp))


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Failed/Cancelled: $failedCount",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(8.dp))

                if (failedCount > 0) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Failed Apps",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                    ) {
                        items(
                            items = failedPackages,
                            key = { it }
                        ) { pkg ->
                            val (label, icon) = try {
                                val appInfo = pm.getApplicationInfo(pkg, 0)
                                val l = pm.getApplicationLabel(appInfo)?.toString() ?: pkg
                                val ic = appInfo.loadIcon(pm)
                                l to ic
                            } catch (_: Exception) {
                                pkg to null
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (icon != null) {
                                    Image(
                                        painter = BitmapPainter(icon.toBitmap().asImageBitmap()),
                                        contentDescription = label,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    // keep alignment consistent
                                    Spacer(Modifier.size(18.dp))
                                }

                                Spacer(Modifier.width(10.dp))

                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}


@Preview
@Composable
private fun PreviewBatchUninstallResultDialog() {
    BatchUninstallResultDialog(
        totalSelected = 5,
        succeededCount = 3,
        failedPackages = listOf(
            "com.example.app1",
            "com.example.app2",
            "com.unknown.app3"
        ),
        onDismiss = {}
    )
}
