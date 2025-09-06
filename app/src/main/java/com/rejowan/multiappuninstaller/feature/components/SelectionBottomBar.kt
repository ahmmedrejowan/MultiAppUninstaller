package com.rejowan.multiappuninstaller.feature.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SelectionBottomBar(
    visible: Boolean,
    selectedCount: Int,
    totalCount: Int,
    allSelected: Boolean,
    onToggleSelectAll: () -> Unit,
    onCancel: () -> Unit,
    onUninstall: () -> Unit,
) {
    AnimatedVisibility(visible = visible, enter = slideInVertically { it }, exit = slideOutVertically { it }) {


        Column(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.background, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .navigationBarsPadding()
                .padding(horizontal = 12.dp)
                .padding(bottom = 4.dp, top = 4.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable(
                        enabled = totalCount > 0,
                        role = Role.RadioButton,
                        onClick = onToggleSelectAll,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() })
                ) {

                    if (allSelected) {
                        Icon(
                            Icons.Default.CheckCircle, contentDescription = "Check", tint = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Uncheck",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = if (allSelected) "Deselect All" else "Select All",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(Modifier.weight(1f))

                Text(
                    text = "$selectedCount / $totalCount selected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(Modifier.padding(vertical = 2.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically
            ) {

                Spacer(Modifier.weight(1f))


                TextButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.Outlined.Close, contentDescription = "Cancel"
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Cancel")
                }

                Spacer(Modifier.weight(0.1f))


                TextButton(
                    onClick = onUninstall, enabled = selectedCount > 0, colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete, contentDescription = "Uninstall"
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Uninstall", style = MaterialTheme.typography.titleSmall.copy(
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun SelectionBottomBarPreview() {
    SelectionBottomBar(
        visible = true,
        selectedCount = 3,
        totalCount = 10,
        allSelected = false,
        onToggleSelectAll = {},
        onCancel = {},
        onUninstall = {})
}