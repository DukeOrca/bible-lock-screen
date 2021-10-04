package com.duke.orca.android.kotlin.biblelockscreen.base.views

import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.duke.orca.android.kotlin.biblelockscreen.bottomnavigation.BottomNavigationPressedListener
import com.duke.orca.android.kotlin.biblelockscreen.bottomnavigation.BottomNavigationWatcher
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.lockscreen.service.LockScreenService

open class LockScreenActivity : AppCompatActivity(), BottomNavigationPressedListener {
    private val bottomNavigationWatcher by lazy { BottomNavigationWatcher(this) }
    private val localBroadcastManager: LocalBroadcastManager by lazy { LocalBroadcastManager.getInstance(this) }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (DataStore.LockScreen.getShowOnLockScreen(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setShowWhenLocked(true)
                getSystemService(KeyguardManager::class.java).requestDismissKeyguard(this, null)
            } else {
                @Suppress("DEPRECATION")
                val flags = WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED

                window.addFlags(flags)
            }
        }
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        bottomNavigationWatcher.setOnNavigationBarPressedListener(this)
        bottomNavigationWatcher.startWatch()
    }

    @CallSuper
    override fun onPause() {
        bottomNavigationWatcher.stopWatch()
        super.onPause()
    }

    @CallSuper
    override fun onDestroy() {
        sendBroadcast(Intent(LockScreenService.Action.MAIN_ACTIVITY_DESTROYED))
        super.onDestroy()
    }

    override fun onHomeKeyPressed() {
        localBroadcastManager.sendBroadcastSync(Intent(LockScreenService.Action.HOME_KEY_PRESSED))
    }

    override fun onRecentAppsPressed() {
        localBroadcastManager.sendBroadcastSync(Intent(LockScreenService.Action.RECENT_APPS_PRESSED))
    }
}