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
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri

@Composable
fun CreditsDialog(onDismiss: () -> Unit) {

    val context = androidx.compose.ui.platform.LocalContext.current

    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(
            text = "Credits", style = MaterialTheme.typography.titleMedium, fontSize = 18.sp
        )
    }, text = {
        Column(
            modifier = Modifier
                .height(400.dp)
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {


            SingleCredit(
                name = "AOSP", license = "Apache License, Version 2.0", onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://www.apache.org/licenses/LICENSE-2.0".toUri())
                    context.startActivity(intent)
                })

            SingleCredit(
                name = "Jetpack Compose",
                version = "2025.09.00 (BOM), 1.9.1 (UI Text Google Fonts), 1.7.8 (Material Icons), 1.9.1 (Runtime LiveData)",
                license = "Apache License, Version 2.0",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://www.apache.org/licenses/LICENSE-2.0".toUri())
                    context.startActivity(intent)
                })

            SingleCredit(
                name = "Accompanist",
                version = "0.36.0 (System UI Controller), 0.30.1 (Insets), 0.37.3 (Permissions)",
                license = "Apache License, Version 2.0",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://www.apache.org/licenses/LICENSE-2.0".toUri())
                    context.startActivity(intent)
                })

            SingleCredit(
                name = "Koin",
                version = "4.1.1",
                license = "Apache License, Version 2.0",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://www.apache.org/licenses/LICENSE-2.0".toUri())
                    context.startActivity(intent)
                })

            SingleCredit(
                name = "Timber",
                version = "5.0.1",
                license = "Apache License, Version 2.0",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://www.apache.org/licenses/LICENSE-2.0".toUri())
                    context.startActivity(intent)
                })

            SingleCredit(
                name = "Timber",
                version = "5.0.1",
                license = "Apache License, Version 2.0",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://www.apache.org/licenses/LICENSE-2.0".toUri())
                    context.startActivity(intent)
                })


            SingleCredit(
                name = "Ekramul Ahasan Tamzid",
                license = "My brother-in-law, who helped me a lot to make this app. Contributed by giving ideas and user perspectives.",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://facebook.com/ekramul.ahasan.2024".toUri())
                    context.startActivity(intent)
                })

            SingleCredit(
                name = "Tasnim Hasan Prottoy",
                license = "My brother-in-law, who helped me a lot to make this app. Contributed by giving ui ideas.",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://facebook.com/tasnimhasan.prottoy.5".toUri())
                    context.startActivity(intent)
                })


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
fun SingleCredit(name: String, version: String? = null, license: String, openText: String = "View", onClick: () -> Unit = {}) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 2.dp)
        )
        version?.let {
            Text(
                text = "Version: $version", style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.size(6.dp))

        Text(
            text = license, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Justify
        )
        Spacer(Modifier.size(4.dp))
        Row(
            Modifier.clickable(onClick = onClick)
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Link,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(6.dp))
            Text(text = openText, color = MaterialTheme.colorScheme.primary)
        }
    }
}


