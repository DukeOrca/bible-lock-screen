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

open class BaseLockScreenActivity : BaseBottomNavigationWatcherActivity() {
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
}