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

package com.rejowan.multiappuninstaller.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rejowan.multiappuninstaller.BuildConfig
import timber.log.Timber

class AppUninstallReceiver(private val onAppUninstalled: (String) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (BuildConfig.DEBUG) {
            Timber.tag(TAG).d("==================================================")
            Timber.tag(TAG).d("📦 BroadcastReceiver triggered")
            Timber.tag(TAG).d("Action: ${intent.action}")
        }

        if (intent.action == Intent.ACTION_PACKAGE_REMOVED) {
            val packageName = intent.data?.schemeSpecificPart

            if (BuildConfig.DEBUG) {
                Timber.tag(TAG).d("✅ ACTION_PACKAGE_REMOVED detected")
                Timber.tag(TAG).d("Package name: $packageName")
            }

            packageName?.let {
                if (BuildConfig.DEBUG) {
                    Timber.tag(TAG).d("🔔 Calling onAppUninstalled callback for: $it")
                }
                onAppUninstalled(it)
                if (BuildConfig.DEBUG) {
                    Timber.tag(TAG).d("✅ Callback executed successfully")
                }
            } ?: run {
                if (BuildConfig.DEBUG) {
                    Timber.tag(TAG).e("❌ Package name is null!")
                }
            }
        }

        if (BuildConfig.DEBUG) {
            Timber.tag(TAG).d("==================================================")
        }
    }

    companion object {
        private const val TAG = "AppUninstallReceiver"
    }
}