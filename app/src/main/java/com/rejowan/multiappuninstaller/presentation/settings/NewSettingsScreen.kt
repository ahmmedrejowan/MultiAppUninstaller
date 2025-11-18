package com.rejowan.multiappuninstaller.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rejowan.multiappuninstaller.presentation.settings.components.AboutSection
import com.rejowan.multiappuninstaller.presentation.settings.components.ThemeSection
import org.koin.androidx.compose.koinViewModel

/**
 * Settings Screen - Refactored
 * Component-based structure following reference projects (Linky, QrCraft, PixelBeam)
 *
 * Sections:
 * - Appearance: Theme and Dynamic Color settings
 * - About: App information, links, and dialogs
 *
 * @param onBackClick Called when back button is clicked
 * @param viewModel Settings ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    // Collect state from ViewModel
    val theme by viewModel.theme.collectAsState()
    val dynamicColorEnabled by viewModel.dynamicColorEnabled.collectAsState()

    // Load preferences on launch
    LaunchedEffect(Unit) {
        viewModel.loadTheme()
        viewModel.setDefaultThemeIfNotSet()
        viewModel.loadDynamicColorPreference()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Theme Section
            ThemeSection(
                currentTheme = theme,
                dynamicColorEnabled = dynamicColorEnabled,
                onThemeChange = { viewModel.saveTheme(it) },
                onDynamicColorChange = { viewModel.saveDynamicColorPreference(it) }
            )

            // About Section
            AboutSection()
        }
    }
}
