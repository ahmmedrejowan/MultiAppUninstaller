package com.rejowan.multiappuninstaller.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Theme settings section
 * Follows reference project patterns (Linky's AppearanceScreen, QRCraft's SettingsScreen)
 * Contains theme mode and dynamic color settings
 *
 * @param currentTheme Current theme setting
 * @param dynamicColorEnabled Whether dynamic color is enabled
 * @param onThemeChange Called when theme is changed
 * @param onDynamicColorChange Called when dynamic color is changed
 * @param modifier Optional modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSection(
    currentTheme: String,
    dynamicColorEnabled: Boolean,
    onThemeChange: (String) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Section Header
    SectionHeader(title = "Appearance", modifier = modifier)

    // Theme Card with Segmented Button
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Theme Title
            Text(
                text = "App Theme",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            // Theme Description
            Text(
                text = "Choose your preferred color theme",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Theme Selection - Segmented Button
            val themes = listOf("System Default", "Light", "Dark")
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                themes.forEachIndexed { index, theme ->
                    SegmentedButton(
                        selected = currentTheme == theme,
                        onClick = { onThemeChange(theme) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = themes.size
                        )
                    ) {
                        Text(
                            text = when (theme) {
                                "System Default" -> "System"
                                else -> theme
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        // Dynamic Color Setting with Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Dynamic Color",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Use colors from your wallpaper",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = dynamicColorEnabled,
                onCheckedChange = onDynamicColorChange
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}
