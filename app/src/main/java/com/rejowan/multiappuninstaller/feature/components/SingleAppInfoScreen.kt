package com.rejowan.multiappuninstaller.feature.components

import android.content.pm.PackageInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.rejowan.multiappuninstaller.utils.DateFormatUtils
import java.io.File

@Composable
fun SingleAppInfoScreen(
    packageInfo: PackageInfo
) {

    val context = LocalContext.current

    val title = packageInfo.applicationInfo?.loadLabel(context.packageManager)?.toString() ?: "App Name"
    val size = (packageInfo.applicationInfo?.sourceDir?.let {
        File(it).length().div(1024f * 1024f).let { formattedSize ->
            String.format("%.2f", formattedSize)
        }
    })?.let { "$it MB" } ?: "Unknown size"
    val packageName = packageInfo.packageName.toString()
    val date = DateFormatUtils.millisToDateTime(packageInfo.firstInstallTime)
    val version = packageInfo.versionName.toString()
    val appIcon = packageInfo.applicationInfo?.loadIcon(context.packageManager)


    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Row(
            modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically
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
//                Text(
//                    text = packageName,
//                    style = MaterialTheme.typography.labelSmall,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    maxLines = 1,
//                    overflow = TextOverflow.MiddleEllipsis
//
//                )
                Text(
                    text = date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }

//            IconButton(
//                onClick = {
//
//                }) {
//                Icon(
//                    Icons.Outlined.Delete, "Delete"
//                )
//
//            }
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

    SingleAppInfoScreen(dummyPackageInfo)
}