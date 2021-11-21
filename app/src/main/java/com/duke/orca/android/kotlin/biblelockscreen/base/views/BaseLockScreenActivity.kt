package com.duke.orca.android.kotlin.biblelockscreen.base.views

import android.app.Activity
import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.CallSuper
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore

open class BaseLockScreenActivity : BaseBottomNavigationWatcherActivity() {
    protected val activity: Activity
        get() = this

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (DataStore.LockScreen.getShowOnLockScreen(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setShowWhenLocked(true)

                // https://stackoverflow.com/questions/60477120/keyguardmanager-memory-leak
                with(getSystemService(KeyguardManager::class.java)) {
                    if (isKeyguardLocked) {
                        requestDismissKeyguard(activity, null)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val flags = WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED

                window.addFlags(flags)
            }
        }
    }
}