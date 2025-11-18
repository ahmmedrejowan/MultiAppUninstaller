package com.rejowan.multiappuninstaller.presentation.settings.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.InvertColors
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Theme settings section
 * Contains theme mode and dynamic color settings
 *
 * @param currentTheme Current theme setting
 * @param dynamicColorEnabled Whether dynamic color is enabled
 * @param onThemeChange Called when theme is changed
 * @param onDynamicColorChange Called when dynamic color is changed
 * @param modifier Optional modifier
 */
@Composable
fun ThemeSection(
    currentTheme: String,
    dynamicColorEnabled: Boolean,
    onThemeChange: (String) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showThemeDropdown by remember { mutableStateOf(false) }
    var showDynamicColorDropdown by remember { mutableStateOf(false) }

    // Section Header
    SectionHeader(title = "Appearance", modifier = modifier)

    // Theme Setting
    SettingItem(
        icon = Icons.Outlined.Palette,
        title = "Theme",
        subtitle = "Your preferred mode: $currentTheme",
        onClick = { showThemeDropdown = true }
    )

    Box {
        DropdownMenu(
            expanded = showThemeDropdown,
            onDismissRequest = { showThemeDropdown = false }
        ) {
            listOf("System Default", "Light", "Dark").forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onThemeChange(option)
                        showThemeDropdown = false
                    }
                )
            }
        }
    }

    // Dynamic Color Setting
    SettingItem(
        icon = Icons.Outlined.InvertColors,
        title = "Color",
        subtitle = "Use Dynamic Color: ${if (dynamicColorEnabled) "Enabled" else "Disabled"}",
        onClick = { showDynamicColorDropdown = true }
    )

    Box {
        DropdownMenu(
            expanded = showDynamicColorDropdown,
            onDismissRequest = { showDynamicColorDropdown = false }
        ) {
            listOf(true, false).forEach { option ->
                DropdownMenuItem(
                    text = { Text(if (option) "Enable" else "Disable") },
                    onClick = {
                        onDynamicColorChange(option)
                        showDynamicColorDropdown = false
                    }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}
