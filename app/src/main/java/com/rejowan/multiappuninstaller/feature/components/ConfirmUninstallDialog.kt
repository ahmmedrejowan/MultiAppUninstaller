package com.rejowan.multiappuninstaller.feature.components

import android.content.pm.PackageInfo
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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
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
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap

@Composable
fun ConfirmUninstallDialog(
    selectedPackages: List<PackageInfo>, onDismiss: () -> Unit, onConfirmUninstall: () -> Unit
) {
    val context = LocalContext.current
    val pm = context.packageManager
    val count = selectedPackages.size

    AlertDialog(onDismissRequest = onDismiss, text = {
        Column(Modifier.fillMaxWidth()) {

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                text = "Uninstall $count Apps",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Do you want to uninstall $count apps?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Review App List",
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
                    items = selectedPackages, key = { it.packageName }) { pkg ->
                    val label = pkg.applicationInfo?.loadLabel(pm)?.toString() ?: pkg.packageName

                    val icon = pkg.applicationInfo?.loadIcon(pm)

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
                            Spacer(Modifier.size(18.dp))
                        }

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = label, style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ), color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }, dismissButton = {
        TextButton(
            onClick = onDismiss,
        ) {
            Icon(Icons.Outlined.Close, contentDescription = "Cancel")
            Spacer(Modifier.width(6.dp))
            Text("Cancel")
        }
    }, confirmButton = {
        TextButton(
            onClick = onConfirmUninstall, colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.error,
            )
        ) {
            Icon(Icons.Outlined.Delete, contentDescription = "Uninstall")
            Spacer(Modifier.width(6.dp))
            Text("Uninstall", color = MaterialTheme.colorScheme.error)
        }
    })
}
