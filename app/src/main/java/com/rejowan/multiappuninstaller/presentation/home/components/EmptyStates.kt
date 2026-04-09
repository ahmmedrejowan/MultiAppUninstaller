/*
 * Multi App Uninstaller
 * Copyright (C) 2025 K M Rejowan Ahmmed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.rejowan.multiappuninstaller.presentation.home.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rejowan.multiappuninstaller.ui.theme.AccentColors
import com.rejowan.multiappuninstaller.ui.theme.SoftAccents

/**
 * Shimmer loading state that mimics the AppListItem layout
 */
@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        ),
        start = Offset(translateAnim - 300f, translateAnim - 300f),
        end = Offset(translateAnim, translateAnim)
    )

    Column(modifier = modifier.fillMaxSize()) {
        repeat(8) {
            ShimmerAppListItem(brush = shimmerBrush)
        }
    }
}

@Composable
private fun ShimmerAppListItem(brush: Brush) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(brush)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // App name placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )

                // Package name badge placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brush)
                )

                // Size + date placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
        }
    }
}

/**
 * Error state display
 *
 * @param errorMessage The error message to display
 */
@Composable
fun ErrorState(
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(AccentColors.Red.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = AccentColors.Red,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Empty search results state
 */
@Composable
fun EmptySearchState(
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(SoftAccents.Purple.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SearchOff,
                    contentDescription = "No results",
                    tint = SoftAccents.Purple,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No apps found",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Try searching for \"$searchQuery\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * No apps installed state
 */
@Composable
fun NoAppsState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(SoftAccents.Teal.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Apps,
                    contentDescription = "No apps",
                    tint = SoftAccents.Teal,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Apps Found",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No user-installed apps detected",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStatesPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
        LoadingState(modifier = Modifier.weight(1f))
        ErrorState(
            errorMessage = "Failed to load apps",
            modifier = Modifier.weight(1f)
        )
        EmptySearchState(
            searchQuery = "Chrome",
            modifier = Modifier.weight(1f)
        )
    }
}
