package com.rejowan.multiappuninstaller.feature.components

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri

@Composable
fun LicenseDialog(onDismiss: () -> Unit) {

    val context = LocalContext.current

    AlertDialog(onDismissRequest = onDismiss, title = {
        Column {
            Text(
                text = "License", style = MaterialTheme.typography.titleMedium, fontSize = 18.sp
            )
            Text(
                text = "Apache License, Version 2.0",
                style = MaterialTheme.typography.titleSmall,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

    }, text = {
        Column(
            modifier = Modifier
                .height(400.dp)
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {

            Text(
                text = "Copyright 2025 K M Rejowan Ahmmed\n\n" + "Licensed under the Apache License, Version 2.0 (the \"License\");\n" + "you may not use this file except in compliance with the License.\n" + "You may obtain a copy of the License at\n\n" + "    http://www.apache.org/licenses/LICENSE-2.0\n\n" + "Unless required by applicable law or agreed to in writing, software\n" + "distributed under the License is distributed on an \"AS IS\" BASIS,\n" + "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" + "See the License for the specific language governing permissions and\n" + "limitations under the License.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, "https://www.apache.org/licenses/LICENSE-2.0".toUri())
                        context.startActivity(intent)
                    }
                    .padding(8.dp)) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = "Link Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "View Full License",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
            }

        }
    }, confirmButton = {
        TextButton(
            onClick = onDismiss
        ) {
            Text("Dismiss")
        }
    })
}


