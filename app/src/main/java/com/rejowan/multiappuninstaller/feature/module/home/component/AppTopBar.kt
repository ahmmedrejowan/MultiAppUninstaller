package com.rejowan.multiappuninstaller.feature.module.home.component

import COutlinedTextField
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    isSearch: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onSettingsClick: () -> Unit,
    focusRequester: FocusRequester
) {
    Crossfade(
        targetState = isSearch, modifier = Modifier.animateContentSize(), label = "Search"
    ) { isSearchActive ->
        if (!isSearchActive) {
            Row(
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .height(80.dp)
                    .fillMaxWidth()
                    .windowInsetsPadding(TopAppBarDefaults.windowInsets), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Multi App Uninstaller",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                    modifier = Modifier.padding(start = 16.dp)
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onSearchToggle) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
            }
        } else {
            COutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                hint = "Search apps...",
                modifier = Modifier
                    .padding(vertical = 6.dp, horizontal = 8.dp)
                    .fillMaxWidth()
                    .height(72.dp)
                    .focusRequester(focusRequester)
                    .windowInsetsPadding(TopAppBarDefaults.windowInsets),
                maxLines = 1,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary),
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface
                ),
                leadingIcon = {
                    IconButton(onClick = onSearchToggle) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                trailingIcon = if (searchQuery.isNotBlank()) {
                    {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear")
                        }
                    }
                } else null)
            LaunchedEffect(isSearchActive) {
                if (isSearchActive) focusRequester.requestFocus()
            }
        }
    }
}