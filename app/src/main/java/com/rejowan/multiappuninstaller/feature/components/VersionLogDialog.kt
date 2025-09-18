package com.rejowan.multiappuninstaller.feature.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VersionLogDialog(onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(
            text = "Version Log", style = MaterialTheme.typography.titleMedium, fontSize = 18.sp
        )
    }, text = {
        Column(
            modifier = Modifier
                .height(400.dp)
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {

            SingleVersionLog(
                version = "Version 0.1.0 ( 1) (2025-09-18)",
                changes = listOf(
                    "Initial release with multi-app uninstallation feature.",
                    "User-friendly interface for selecting and uninstalling multiple apps.",
                    "Confirmation dialogs to prevent accidental uninstalls or cancellations.",
                )
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
fun SingleVersionLog(version: String, changes: List<String>) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = version,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        changes.forEach { change ->
            Text(
                text = "• $change",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}


