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
fun HowToUseDialog(onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(
            text = "How to use", style = MaterialTheme.typography.titleMedium, fontSize = 18.sp
        )
    }, text = {
        // The content of the dialog, which is scrollable
        Column(
            modifier = Modifier
                .height(400.dp)
                .verticalScroll(rememberScrollState()) // Make content scrollable within 400dp height
                .padding(16.dp)
        ) {
            // First section: App Info
            Text(
                text = "App Info", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Click on App's name to open the application info dialog and see the application info, and relevant information of the app.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Second section: Single Install
            Text(
                text = "Single Install",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "From App Info Dialog, click uninstall button to uninstall that particular app.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Third section: Multi Uninstall
            Text(
                text = "Multi Uninstall",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Long press an app to enter selection mode. Select all the apps you want to uninstall, check and review if the list is correct. If everything is fine, confirm Uninstall for each of the apps as it is prompted to you.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
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
