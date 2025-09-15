package com.rejowan.multiappuninstaller.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AppUninstallReceiver(private val onAppUninstalled: (String) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_PACKAGE_REMOVED) {
            val packageName = intent.data?.schemeSpecificPart
            packageName?.let {
                onAppUninstalled(it)
            }
        }
    }
}