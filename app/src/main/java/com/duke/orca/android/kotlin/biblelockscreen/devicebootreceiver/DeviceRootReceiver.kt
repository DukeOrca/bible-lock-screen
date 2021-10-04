package com.duke.orca.android.kotlin.biblelockscreen.devicebootreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.duke.orca.android.kotlin.biblelockscreen.lockscreen.service.LockScreenService

class DeviceBootReceiver : BroadcastReceiver()  {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            startService(context)
        }
    }

    private fun startService(context: Context) {
        val intent = Intent(context, LockScreenService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}