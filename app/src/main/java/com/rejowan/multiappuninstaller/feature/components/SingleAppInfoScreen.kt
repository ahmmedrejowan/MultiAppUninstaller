package com.rejowan.multiappuninstaller.feature.components

import android.content.pm.PackageInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.rejowan.multiappuninstaller.utils.DateFormatUtils
import java.io.File

@Composable
fun SingleAppInfoScreen(
    modifier: Modifier = Modifier,
    packageInfo: PackageInfo,
    isSelecting: Boolean = false,
    isSelected: Boolean = false,
    onToggle: () -> Unit = {},
    onStartSelection: (() -> Unit)? = null,
    onNormalClick: (() -> Unit)? = null,
) {

    val context = LocalContext.current

    val title = packageInfo.applicationInfo?.loadLabel(context.packageManager)?.toString() ?: "App Name"
    val size = (packageInfo.applicationInfo?.sourceDir?.let {
        File(it).length().div(1024f * 1024f).let { formattedSize ->
            String.format("%.2f", formattedSize)
        }
    })?.let { "$it MB" } ?: "Unknown size"
    val installDate = DateFormatUtils.millisToDateTime(packageInfo.firstInstallTime)
    val updateDate = DateFormatUtils.millisToDateTime(packageInfo.lastUpdateTime)
    val appIcon = packageInfo.applicationInfo?.loadIcon(context.packageManager)


    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 8.dp)
            .combinedClickable(onClick = {
                if (isSelecting) onToggle() else onNormalClick?.invoke()
            }, onLongClick = {
                if (!isSelecting) onStartSelection?.invoke() else onToggle()
            }), shape = RoundedCornerShape(8.dp), colors = CardDefaults.elevatedCardColors().copy(
        containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    )) {

        Row(
            modifier = Modifier.padding(4.dp)
        ) {

            if (appIcon != null) {
                Image(
                    painter = BitmapPainter(appIcon.toBitmap().asImageBitmap()),
                    contentDescription = title,
                    modifier = Modifier
                        .padding(8.dp)
                        .padding(end = 8.dp)
                        .size(48.dp)
                )
            }


            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.padding(1.dp))
                Text(
                    text = size, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.padding(1.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    if (installDate == updateDate) {
                        Column {
                            Text(
                                text = installDate,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                lineHeight = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Installed and Last Updated at",
                                lineHeight = 9.sp,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                    } else {
                        Column {
                            Text(
                                text = installDate,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                lineHeight = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Installed at",
                                lineHeight = 9.sp,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        VerticalDivider(
                            modifier = Modifier
                                .height(8.dp)
                                .padding(horizontal = 4.dp)
                        )

                        Column {
                            Text(
                                text = updateDate,
                                lineHeight = 10.sp,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Last Updated at",
                                lineHeight = 9.sp,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }


                }


            }

            if (isSelected) {

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Checked",
                    tint = MaterialTheme.colorScheme.primary,
                )


            }

        }

    }

}


@Preview(showBackground = true)
@Composable
fun SingleAppInfoScreenPreview() {
    val dummyPackageInfo = PackageInfo()

    dummyPackageInfo.versionName = "1.0.0"
    dummyPackageInfo.packageName = "com.example.app"
    dummyPackageInfo.firstInstallTime = System.currentTimeMillis()

    SingleAppInfoScreen(
        packageInfo = dummyPackageInfo, isSelecting = true, isSelected = true
    )
}


@Preview(showBackground = true)
@Composable
fun SingleAppInfoScreenPreview2() {
    val dummyPackageInfo = PackageInfo()

    dummyPackageInfo.versionName = "1.0.0"
    dummyPackageInfo.packageName = "com.example.app"
    dummyPackageInfo.firstInstallTime = System.currentTimeMillis()

    SingleAppInfoScreen(
        packageInfo = dummyPackageInfo, isSelecting = true, isSelected = false
    )
}