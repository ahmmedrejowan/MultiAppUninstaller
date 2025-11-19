package com.rejowan.multiappuninstaller.presentation.home.components

import android.content.pm.PackageInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.rejowan.multiappuninstaller.utils.DateFormatUtils
import java.io.File

/**
 * Displays a single app item in the list with Material 3 card design.
 *
 * @param packageInfo The package information for the app
 * @param isSelecting Whether selection mode is active
 * @param isSelected Whether this app is currently selected
 * @param onToggle Called when the item is clicked in selection mode
 * @param onStartSelection Called when long-pressed to start selection mode
 * @param onNormalClick Called when clicked in normal mode
 * @param modifier Optional modifier for the composable
 */
@Composable
fun AppListItem(
    packageInfo: PackageInfo,
    isSelecting: Boolean = false,
    isSelected: Boolean = false,
    onToggle: () -> Unit = {},
    onStartSelection: () -> Unit = {},
    onNormalClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val appName = packageInfo.applicationInfo?.loadLabel(context.packageManager)?.toString()
        ?: "Unknown App"
    val packageName = packageInfo.packageName
    val appIcon = packageInfo.applicationInfo?.loadIcon(context.packageManager)

    // Calculate app size
    val appSize = packageInfo.applicationInfo?.sourceDir?.let { sourceDir ->
        File(sourceDir).length().div(1024f * 1024f).let { sizeInMB ->
            String.format("%.2f MB", sizeInMB)
        }
    } ?: "Unknown size"

    // Format dates
    val installDate = DateFormatUtils.millisToDateTime(packageInfo.firstInstallTime)

    // Card with M3 design
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = {
                    if (isSelecting) onToggle() else onNormalClick()
                },
                onLongClick = {
                    if (!isSelecting) onStartSelection() else onToggle()
                }
            ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.outlinedCardElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            if (appIcon != null) {
                Image(
                    painter = BitmapPainter(appIcon.toBitmap().asImageBitmap()),
                    contentDescription = "$appName icon",
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // App Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // App Name
                Text(
                    text = appName,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Package Name
                Text(
                    text = packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Size and Install Date
                Text(
                    text = "$appSize • $installDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            // Selection Indicator
            if (isSelecting) {
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggle() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppListItemPreview() {
    val dummyPackageInfo = PackageInfo().apply {
        packageName = "com.example.app"
        firstInstallTime = System.currentTimeMillis()
    }

    Column {
        AppListItem(
            packageInfo = dummyPackageInfo,
            isSelecting = false,
            isSelected = false
        )

        AppListItem(
            packageInfo = dummyPackageInfo,
            isSelecting = true,
            isSelected = true
        )
    }
}
