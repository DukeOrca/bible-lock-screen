package com.duke.orca.android.kotlin.biblelockscreen.base.views

import android.content.Intent
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.duke.orca.android.kotlin.biblelockscreen.bottomnavigation.BottomNavigationPressedListener
import com.duke.orca.android.kotlin.biblelockscreen.bottomnavigation.BottomNavigationWatcher
import com.duke.orca.android.kotlin.biblelockscreen.lockscreen.service.LockScreenService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class BaseBottomNavigationWatcherActivity : AppCompatActivity(), BottomNavigationPressedListener {
    private val bottomNavigationWatcher by lazy { BottomNavigationWatcher(this) }
    private val localBroadcastManager: LocalBroadcastManager by lazy { LocalBroadcastManager.getInstance(this) }

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

    protected fun delayOnLifecycle(
        timeMillis: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        block: () -> Unit
    ) {
        lifecycle.coroutineScope.launch(dispatcher) {
            delay(timeMillis)
            block()
        }
    }
}