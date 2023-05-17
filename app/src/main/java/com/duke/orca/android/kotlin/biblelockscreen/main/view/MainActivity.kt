package com.duke.orca.android.kotlin.biblelockscreen.main.view

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseLockScreenActivity
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.eventbus.BehaviourEventBus
import com.duke.orca.android.kotlin.biblelockscreen.lockscreen.service.LockScreenService
import com.duke.orca.android.kotlin.biblelockscreen.main.viewmodel.MainViewModel
import com.duke.orca.android.kotlin.biblelockscreen.networkstatus.NetworkStatus
import com.duke.orca.android.kotlin.biblelockscreen.networkstatus.NetworkStatusTracker
import com.duke.orca.android.kotlin.biblelockscreen.permission.PermissionChecker
import com.duke.orca.android.kotlin.biblelockscreen.permission.view.PermissionRationaleDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseLockScreenActivity(), PermissionRationaleDialogFragment.OnPermissionAllowClickListener {
    private val viewModel by viewModels<MainViewModel>()

    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BehaviourEventBus.newInstance()

        if (PermissionRationaleDialogFragment.permissionsGranted(this).not()) {
            PermissionRationaleDialogFragment().also {
                it.show(supportFragmentManager, it.tag)
            }
        }

        NetworkStatusTracker(applicationContext).networkStatus
            .asLiveData(lifecycleScope.coroutineContext)
            .observe(this) {
                with(viewModel) {
                    when (it) {
                        NetworkStatus.Available -> billingModule.startConnection()
                        NetworkStatus.Unavailable -> billingModule.endConnection()
                    }
                }
            }
    }

    override fun onStart() {
        super.onStart()

        if (DataStore.isFirstTime(this)) {
            DataStore.putFirstTime(this, false)
        }
    }

    override fun onDestroy() {
        BehaviourEventBus.clear()
        viewModel.billingModule.removeCallback()
        super.onDestroy()
    }

    override fun onPermissionAllowClick() {
        checkManageOverlayPermission()
    }

    override fun onPermissionDenyClick() {
        if (PermissionChecker.hasManageOverlayPermission()) {
            startService()
        } else {
            finish()
        }
    }

    private fun startService() {
        val intent = Intent(applicationContext, LockScreenService::class.java)

        startForegroundService(intent)
    }

    private fun checkManageOverlayPermission() {
        if (PermissionChecker.hasManageOverlayPermission())
            startService()
        else {
            val uri = Uri.fromParts("package", packageName, null)
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            }

            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

            handler = Handler(mainLooper)

            handler?.postDelayed(object : Runnable {
                override fun run() {
                    if (Settings.canDrawOverlays(this@MainActivity)) {
                        Intent(this@MainActivity, MainActivity::class.java).run {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                            val customAnimation = ActivityOptions.makeCustomAnimation(
                                this@MainActivity,
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )

                            startActivity(this, customAnimation.toBundle())
                        }

                        handler = null
                        return
                    }

                    handler?.postDelayed(this, Duration.LONG)
                }
            }, Duration.LONG)
        }
    }
}