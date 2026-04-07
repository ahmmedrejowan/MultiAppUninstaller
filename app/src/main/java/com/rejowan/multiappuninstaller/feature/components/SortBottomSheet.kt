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

package com.rejowan.multiappuninstaller.feature.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.SortByAlpha
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejowan.multiappuninstaller.presentation.home.SortType
import com.rejowan.multiappuninstaller.ui.theme.SoftAccents

/**
 * Sort options bottom sheet with categorized sort options.
 * Adapted from Linky's SortOptionsSheet design.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    currentSortType: SortType,
    sortAscending: Boolean,
    onSortSelected: (SortType, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            SortSheetHeader()

            Spacer(modifier = Modifier.height(12.dp))

            // Name Sort
            SortCategoryCard(
                title = "Alphabetical",
                description = "Sort by app name",
                icon = Icons.Outlined.SortByAlpha,
                accentColor = SoftAccents.Purple,
                sortType = SortType.NAME,
                currentSortType = currentSortType,
                sortAscending = sortAscending,
                ascLabel = "A → Z",
                descLabel = "Z → A",
                onSortSelected = { type, asc ->
                    onSortSelected(type, asc)
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Size Sort
            SortCategoryCard(
                title = "App Size",
                description = "Sort by storage usage",
                icon = Icons.Outlined.Storage,
                accentColor = SoftAccents.Teal,
                sortType = SortType.SIZE,
                currentSortType = currentSortType,
                sortAscending = sortAscending,
                ascLabel = "Smallest",
                descLabel = "Largest",
                onSortSelected = { type, asc ->
                    onSortSelected(type, asc)
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Install Date Sort
            SortCategoryCard(
                title = "Install Date",
                description = "Sort by first install time",
                icon = Icons.Outlined.AccessTime,
                accentColor = SoftAccents.Blue,
                sortType = SortType.INSTALL_DATE,
                currentSortType = currentSortType,
                sortAscending = sortAscending,
                ascLabel = "Oldest",
                descLabel = "Newest",
                onSortSelected = { type, asc ->
                    onSortSelected(type, asc)
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Update Date Sort
            SortCategoryCard(
                title = "Update Date",
                description = "Sort by last update time",
                icon = Icons.Outlined.Update,
                accentColor = SoftAccents.Amber,
                sortType = SortType.UPDATE_DATE,
                currentSortType = currentSortType,
                sortAscending = sortAscending,
                ascLabel = "Oldest",
                descLabel = "Newest",
                onSortSelected = { type, asc ->
                    onSortSelected(type, asc)
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun SortSheetHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Sort,
                contentDescription = null,
                modifier = Modifier
                    .padding(6.dp)
                    .size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = "Sort Apps",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Choose how to sort your apps",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun SortCategoryCard(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    sortType: SortType,
    currentSortType: SortType,
    sortAscending: Boolean,
    ascLabel: String,
    descLabel: String,
    onSortSelected: (SortType, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val isActiveCategory = sortType == currentSortType

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isActiveCategory) {
            accentColor.copy(alpha = 0.06f)
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Category header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.12f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(6.dp)
                            .size(16.dp),
                        tint = accentColor
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                if (isActiveCategory) {
                    Surface(
                        shape = CircleShape,
                        color = accentColor.copy(alpha = 0.15f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "Selected",
                            modifier = Modifier
                                .padding(4.dp)
                                .size(14.dp),
                            tint = accentColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sort direction options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SortOptionChip(
                    label = ascLabel,
                    icon = Icons.Rounded.ArrowUpward,
                    isSelected = isActiveCategory && sortAscending,
                    accentColor = accentColor,
                    onClick = { onSortSelected(sortType, true) },
                    modifier = Modifier.weight(1f)
                )
                SortOptionChip(
                    label = descLabel,
                    icon = Icons.Rounded.ArrowDownward,
                    isSelected = isActiveCategory && !sortAscending,
                    accentColor = accentColor,
                    onClick = { onSortSelected(sortType, false) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SortOptionChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) accentColor.copy(alpha = 0.12f)
        else MaterialTheme.colorScheme.surfaceContainerHigh,
        animationSpec = tween(200),
        label = "chip bg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) accentColor.copy(alpha = 0.5f)
        else Color.Transparent,
        animationSpec = tween(200),
        label = "chip border"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) accentColor
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "chip content"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(
                    width = 1.5.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(10.dp)
                ) else Modifier
            ),
        shape = RoundedCornerShape(10.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = contentColor
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                ),
                color = contentColor
            )

            if (isSelected) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Selected",
                        modifier = Modifier.size(9.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}
