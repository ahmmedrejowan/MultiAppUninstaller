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

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rejowan.multiappuninstaller.presentation.home.SortType

/**
 * Filter section with horizontal scrolling chips for sorting options.
 *
 * @param selectedSortType The currently selected sort type
 * @param sortAscending Whether sorting is in ascending order
 * @param onSortTypeChange Called when sort type is changed
 * @param onSortDirectionToggle Called when sort direction is toggled
 * @param modifier Optional modifier for the composable
 */
@Composable
fun FilterSection(
    selectedSortType: SortType,
    sortAscending: Boolean,
    onSortTypeChange: (SortType) -> Unit,
    onSortDirectionToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sort direction toggle
        IconButton(onClick = onSortDirectionToggle) {
            Icon(
                imageVector = if (sortAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = if (sortAscending) "Ascending" else "Descending",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Sort by Name
        FilterChip(
            selected = selectedSortType == SortType.NAME,
            onClick = { onSortTypeChange(SortType.NAME) },
            label = { Text("Name") }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Sort by Size
        FilterChip(
            selected = selectedSortType == SortType.SIZE,
            onClick = { onSortTypeChange(SortType.SIZE) },
            label = { Text("Size") }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Sort by Install Date
        FilterChip(
            selected = selectedSortType == SortType.INSTALL_DATE,
            onClick = { onSortTypeChange(SortType.INSTALL_DATE) },
            label = { Text("Install Date") }
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Sort by Update Date
        FilterChip(
            selected = selectedSortType == SortType.UPDATE_DATE,
            onClick = { onSortTypeChange(SortType.UPDATE_DATE) },
            label = { Text("Update Date") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FilterSectionPreview() {
    FilterSection(
        selectedSortType = SortType.NAME,
        sortAscending = true,
        onSortTypeChange = {},
        onSortDirectionToggle = {}
    )
}
