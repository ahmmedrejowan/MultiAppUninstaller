package com.rejowan.multiappuninstaller.feature.components

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.rejowan.multiappuninstaller.R

@Composable
fun CreatorDialog(onDismiss: () -> Unit) {

    val context = LocalContext.current

    AlertDialog(onDismissRequest = onDismiss, title = {

        Row {

            Image(
                painter = painterResource(id = R.drawable.ic_rejowan),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterVertically)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = "K M Rejowan Ahmmed", style = MaterialTheme.typography.titleMedium, fontSize = 18.sp
                )
                Text(
                    text = "@ahmmedrejowan", style = MaterialTheme.typography.titleSmall, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        }

    }, text = {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .verticalScroll(rememberScrollState())
        ) {

            SingleCreatorInfo(
                icon = R.drawable.ic_github,
                title = "GitHub",
                subtitle = "@ahmmedrejowan",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://github.com/ahmmedrejowan".toUri())
                    context.startActivity(intent)
                }
            )

            SingleCreatorInfo(
                icon = R.drawable.ic_web,
                title = "Website",
                subtitle = "rejowan.com",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://rejowan.com/".toUri())
                    context.startActivity(intent)
                }
            )

            SingleCreatorInfo(
                icon = R.drawable.ic_fb,
                title = "Facebook",
                subtitle = "@ahmmedrejowan",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://facebook.com/ahmmedrejowan".toUri())
                    context.startActivity(intent)
                }
            )

            SingleCreatorInfo(
                icon = R.drawable.ic_linkedin,
                title = "LinkedIn",
                subtitle = "@ahmmedrejowan",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://linkedin.com/in/ahmmedrejowan"
                        .toUri())
                    context.startActivity(intent)
                }
            )

            SingleCreatorInfo(
                icon = R.drawable.ic_yt,
                title = "YouTube",
                subtitle = "Tranquilly Coding",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://youtube.com/@TranquillyCoding".
                    toUri())
                    context.startActivity(intent)
                }
            )




        }
    }, confirmButton = {
        TextButton(
            onClick = onDismiss
        ) {
            Text("Dismiss")
        }
    })
}


@Composable
private fun SingleCreatorInfo(
    icon: Int, title: String, subtitle: String, onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 3.dp, bottom = 3.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp).padding(start = 12.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
