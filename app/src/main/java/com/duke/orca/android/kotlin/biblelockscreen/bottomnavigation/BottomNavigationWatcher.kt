package com.duke.orca.android.kotlin.biblelockscreen.bottomnavigation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class BottomNavigationWatcher(private val context: Context) {
    private val intentFilter: IntentFilter by lazy { IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS) }

    private var bottomNavigationPressedListener: BottomNavigationPressedListener? = null
    private var innerBroadcastReceiver: InnerBroadcastReceiver? = null

    fun setOnNavigationBarPressedListener(bottomNavigationPressedListener: BottomNavigationPressedListener) {
        this.bottomNavigationPressedListener = bottomNavigationPressedListener
    }

    fun startWatch() {
        bottomNavigationPressedListener?.let {
            innerBroadcastReceiver = InnerBroadcastReceiver()
            context.registerReceiver(innerBroadcastReceiver, intentFilter)
        }
    }

    fun stopWatch() {
        bottomNavigationPressedListener?.let {
            innerBroadcastReceiver?.let {
                context.unregisterReceiver(it)
                innerBroadcastReceiver = null
            }
        }
    }

    internal inner class InnerBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action

            if (action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                val systemDialogReasonKey = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY) ?: return

                bottomNavigationPressedListener?.let {
                    when(systemDialogReasonKey) {
                        SYSTEM_DIALOG_REASON_HOME_KEY -> it.onHomeKeyPressed()
                        SYSTEM_DIALOG_REASON_RECENT_APPS -> it.onRecentAppsPressed()
                    }
                }
            }
        }
    }

    companion object {
        private const val SYSTEM_DIALOG_REASON_KEY = "reason"
        private const val SYSTEM_DIALOG_REASON_HOME_KEY = "homekey"
        private const val SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps"
    }
}