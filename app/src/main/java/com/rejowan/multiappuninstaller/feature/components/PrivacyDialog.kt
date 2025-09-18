package com.rejowan.multiappuninstaller.feature.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyDialog(onDismiss: () -> Unit) {

    LocalContext.current

    AlertDialog(onDismissRequest = onDismiss, title = {
        Column {
            Text(
                text = "Privacy Policy", style = MaterialTheme.typography.titleMedium, fontSize = 18.sp
            )
            Text(
                text = "How we handle your data",
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
                text = "Information Collection and Use",
                style = MaterialTheme.typography.titleMedium,

            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = "The Application does not collect, store, or process any personal information from users. It only accesses the list of installed apps on your device to provide the uninstall feature.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
            Spacer(Modifier.size(12.dp))

            Text(
                text = "Location Information",
                style = MaterialTheme.typography.titleMedium,

            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = "The Application does not collect any location information from your device.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
            Spacer(Modifier.size(12.dp))

            Text(
                text = "Third-Party Access",
                style = MaterialTheme.typography.titleMedium,

            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = "Since the Application does not collect any data, no information is shared with third parties.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
            Spacer(Modifier.size(12.dp))

            Text(
                text = "Opt-Out Rights",
                style = MaterialTheme.typography.titleMedium,

            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = "You can stop using the Application by uninstalling it using your device's standard uninstall process or through the mobile application marketplace.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
            Spacer(Modifier.size(12.dp))

            Text(
                text = "Children",
                style = MaterialTheme.typography.titleMedium,

            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = "The Application does not target or knowingly collect information from children under 13. Parents and guardians are encouraged to monitor their children's use of the Application.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
            Spacer(Modifier.size(12.dp))

            Text(
                text = "Security",
                style = MaterialTheme.typography.titleMedium,

            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = "As the Application does not collect or store any data, there is no risk of unauthorized access to user information.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
            Spacer(Modifier.size(12.dp))

            Text(
                text = "Changes to This Policy",
                style = MaterialTheme.typography.titleMedium,

            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = "This Privacy Policy may be updated periodically. Changes will be posted on this page, and continued use of the Application constitutes acceptance of any updates.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
            Spacer(Modifier.size(12.dp))

            Text(
                text = "Contact Us",
                style = MaterialTheme.typography.titleMedium,

            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = "For questions about this Privacy Policy, contact the Service Provider at kmrejowan@gmail.com.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
            Spacer(Modifier.size(12.dp))

            Text(
                text = "Effective Date",
                style = MaterialTheme.typography.titleMedium,

            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = "This policy is effective as of September 18, 2025.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
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


