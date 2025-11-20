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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rejowan.multiappuninstaller.utils.SortConfig
import com.rejowan.multiappuninstaller.utils.SortKey
import com.rejowan.multiappuninstaller.utils.SortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBar(
    sortConfig: SortConfig, onChange: (SortConfig) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {


        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            val keys = listOf(
                SortKey.NAME to "Name", SortKey.SIZE to "Size", SortKey.INSTALLED to "Installed", SortKey.UPDATED to "Updated"
            )
            keys.forEachIndexed { index, (key, label) ->
                val selected = sortConfig.key == key
                SegmentedButton(
                    modifier = Modifier.height(36.dp),
                    selected = selected,
                    onClick = { onChange(sortConfig.copy(key = key)) },
                    shape = SegmentedButtonDefaults.itemShape(index, keys.size),
                    label = {
                        Text(
                            text = label, style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                        )
                    },
                    icon = {

                    })
            }
        }

        Spacer(Modifier.height(2.dp))

        Box(
            modifier = Modifier.fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.CenterEnd
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.wrapContentWidth()
            ) {
                SegmentedButton(selected = sortConfig.order == SortOrder.ASC, onClick = {
                    onChange(sortConfig.copy(order = SortOrder.ASC))
                }, shape = SegmentedButtonDefaults.itemShape(0, 2), label = {
                    Text(
                        "ASC", style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp)
                    )
                }, modifier = Modifier.height(28.dp), icon = {

                })

                // DESC Button (single choice)
                SegmentedButton(selected = sortConfig.order == SortOrder.DESC, onClick = {
                    onChange(sortConfig.copy(order = SortOrder.DESC))
                }, shape = SegmentedButtonDefaults.itemShape(1, 2), label = {
                    Text(
                        "DESC", style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp)
                    )
                }, modifier = Modifier.height(28.dp), icon = {

                })
            }
        }

    }
}
