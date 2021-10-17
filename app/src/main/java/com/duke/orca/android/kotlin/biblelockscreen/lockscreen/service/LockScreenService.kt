package com.duke.orca.android.kotlin.biblelockscreen.lockscreen.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.provider.Settings
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.lockscreen.blindscreen.BlindScreenPresenter
import com.duke.orca.android.kotlin.biblelockscreen.main.view.MainActivity
import com.duke.orca.android.kotlin.biblelockscreen.permission.PermissionChecker
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class LockScreenService : Service() {
    object Action {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.lockscreen.service"
        const val HOME_KEY_PRESSED = "$PACKAGE_NAME.HOME_KEY_PRESSED"
        const val MAIN_ACTIVITY_DESTROYED = "$PACKAGE_NAME.MAIN_ACTIVITY_DESTROYED"
        const val RECENT_APPS_PRESSED = "$PACKAGE_NAME.RECENT_APPS_PRESSED"
        const val STOP_SELF = "$PACKAGE_NAME.STOP_SELF"
    }

    private val blindScreenPresenter = BlindScreenPresenter()
    private val localBroadcastManager: LocalBroadcastManager by lazy {
        LocalBroadcastManager.getInstance(this)
    }

    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private var disposable: Disposable? = null

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val showOnLockScreen = DataStore.LockScreen.getShowOnLockScreen(context)
            val displayAfterUnlocking = DataStore.LockScreen.getDisplayAfterUnlocking(context)

            when (intent.action) {
                Action.MAIN_ACTIVITY_DESTROYED -> {
                    if (PermissionChecker.hasManageOverlayPermission().not()) {
                        NotificationBuilder.ManageOverlayPermission.create(context).build().run {
                            notificationManager.notify(NotificationBuilder.ManageOverlayPermission.ID, this)
                        }
                    }
                }
                Action.STOP_SELF -> {
                    stopSelf()
                    notificationManager.cancel(NotificationBuilder.ID)
                }
                Intent.ACTION_SCREEN_OFF -> {
                    if (showOnLockScreen.not() || displayAfterUnlocking)
                        return

                    Intent(context, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(this)
                    }
                }
                Intent.ACTION_USER_PRESENT -> {
                    if (showOnLockScreen.not() || displayAfterUnlocking.not())
                        return

                    Intent(context, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(this)
                    }
                }
                else -> {

                }
            }
        }
    }

    private val bottomNavigationBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                Action.HOME_KEY_PRESSED, Action.RECENT_APPS_PRESSED -> {
                    context ?: return

                    val showOnLockScreen = DataStore.LockScreen.getShowOnLockScreen(context)

                    if (showOnLockScreen && Settings.canDrawOverlays(context)) {
                        blindScreenPresenter.show()
                    }
                }
                else -> {

                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        localBroadcastManager.registerReceiver(bottomNavigationBroadcastReceiver, IntentFilter().apply {
            addAction(Action.HOME_KEY_PRESSED)
            addAction(Action.RECENT_APPS_PRESSED)
        })

        registerReceiver(
            broadcastReceiver,
            IntentFilter().apply {
                addAction(Action.MAIN_ACTIVITY_DESTROYED)
                addAction(Action.STOP_SELF)
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_USER_PRESENT)
            }
        )

        disposable = NotificationBuilder.single(this, notificationManager)
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                startForeground(NotificationBuilder.ID, it.build())
            }) {
                Timber.e(it)
            }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        try {
            blindScreenPresenter.hide()
            localBroadcastManager.unregisterReceiver(bottomNavigationBroadcastReceiver)
            unregisterReceiver(broadcastReceiver)
        } catch (e: IllegalArgumentException) {
            Timber.e(e)
        } finally {
            disposable?.dispose()
        }

        blindScreenPresenter.isBlindScreenVisible = false

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}