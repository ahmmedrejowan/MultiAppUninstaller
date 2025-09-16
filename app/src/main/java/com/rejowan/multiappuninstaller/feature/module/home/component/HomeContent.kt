package com.rejowan.multiappuninstaller.feature.module.home.component

import android.content.pm.PackageInfo
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.unit.dp
import com.rejowan.multiappuninstaller.feature.components.AppDetailsDialog
import com.rejowan.multiappuninstaller.feature.components.BatchUninstallResultDialog
import com.rejowan.multiappuninstaller.feature.components.CancelConfirmationDialog
import com.rejowan.multiappuninstaller.feature.components.ConfirmUninstallDialog
import com.rejowan.multiappuninstaller.feature.components.ExitConfirmationDialog
import com.rejowan.multiappuninstaller.feature.components.HowToUseDialog
import com.rejowan.multiappuninstaller.feature.components.SingleAppInfoScreen
import com.rejowan.multiappuninstaller.feature.components.SortBar
import com.rejowan.multiappuninstaller.utils.SortConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    appListLoading: Boolean,
    appListError: String?,
    filteredApps: List<PackageInfo>,
    sortConfig: SortConfig,
    onSortConfigChange: (SortConfig) -> Unit,
    isSearch: Boolean,
    searchQuery: String,
    onSearchToggle: () -> Unit,
    focusManager: FocusManager,
    isSelecting: Boolean,
    selectedApps: Set<String>,
    onToggleSelection: (String) -> Unit,
    onStartSelection: (String) -> Unit,
    modifier: Modifier = Modifier,
    showExitBottomSheet: Boolean,
    onDismissExitBottomSheet: () -> Unit,
    onDismissCancelConfirmationDialog: () -> Unit,
    onExit: () -> Unit,
    onExitCancelConfirmationDialog: () -> Unit,
    showCancelConfirmationDialog: Boolean,
    showUninstallConfirmationDialog: Boolean,
    onDismissUninstallConfirmationDialog: () -> Unit,
    onConfirmUninstall: () -> Unit,
    showBatchResultDialog: Boolean,
    totalSelectedAtStart: Int,
    succeededCount: Int,
    failedPackages: List<String>,
    onDismissUninstallResultDialog: () -> Unit,
    showFirstTutorial: Boolean,
    onDismissFirstTutorial: () -> Unit
    ) {


    var detailsFor by remember { mutableStateOf<PackageInfo?>(null) }


    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            when {
                appListLoading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }

                appListError != null -> {
                    Text(
                        text = appListError, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() }, indication = null
                            ) {
                                if (searchQuery.isEmpty()) onSearchToggle()
                                focusManager.clearFocus()
                            }) {
                        item {
                            SortBar(
                                sortConfig = sortConfig, onChange = onSortConfigChange
                            )
                        }
                        items(items = filteredApps, key = { it.packageName }) { appInfo ->

                            val isSelected = selectedApps.contains(appInfo.packageName)

                            SingleAppInfoScreen(
                                packageInfo = appInfo,
                                isSelecting = isSelecting,
                                isSelected = isSelected,
                                onToggle = { onToggleSelection(appInfo.packageName) },
                                onStartSelection = { onStartSelection(appInfo.packageName) },
                                onNormalClick = {
                                    detailsFor = appInfo
                                })
                        }

                    }
                }
            }
        }

        if (showExitBottomSheet) {
            ModalBottomSheet(onDismissRequest = onDismissExitBottomSheet) {
                ExitConfirmationDialog(
                    onCancel = onDismissExitBottomSheet, onExit = onExit
                )
            }
        }

        if (showCancelConfirmationDialog) {
            ModalBottomSheet(onDismissRequest = onDismissCancelConfirmationDialog) {
                CancelConfirmationDialog(
                    totalSelectedApps = selectedApps.size,
                    onCancel = onDismissCancelConfirmationDialog,
                    onExit = onExitCancelConfirmationDialog
                )

            }
        }

        if (showUninstallConfirmationDialog) {

            val selectedAppInfos = filteredApps.filter { selectedApps.contains(it.packageName) }

            ConfirmUninstallDialog(
                selectedPackages = selectedAppInfos,
                onDismiss = onDismissUninstallConfirmationDialog,
                onConfirmUninstall = onConfirmUninstall
            )
        }

        if (showBatchResultDialog) {
            BatchUninstallResultDialog(
                totalSelected = totalSelectedAtStart,
                succeededCount = succeededCount,
                failedPackages = failedPackages,
                onDismiss = onDismissUninstallResultDialog
            )
        }

        if (showFirstTutorial){
            Log.e("HomeContent","Showing first tutorial dialog")
             HowToUseDialog(onDismiss = {
                onDismissFirstTutorial()
             })
        }


        detailsFor?.let { pkg ->
            AppDetailsDialog(
                packageInfo = pkg,
                onDismiss = { detailsFor = null }
                )
        }


    }
}