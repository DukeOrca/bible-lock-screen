package com.duke.orca.android.kotlin.biblelockscreen.main.view

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.provider.Settings.EXTRA_APP_PACKAGE
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseLockScreenActivity
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.eventbus.BehaviourEventBus
import com.duke.orca.android.kotlin.biblelockscreen.extension.checkPermission
import com.duke.orca.android.kotlin.biblelockscreen.lockscreen.service.LockScreenService
import com.duke.orca.android.kotlin.biblelockscreen.main.viewmodel.MainViewModel
import com.duke.orca.android.kotlin.biblelockscreen.networkstatus.NetworkStatus
import com.duke.orca.android.kotlin.biblelockscreen.networkstatus.NetworkStatusTracker
import com.duke.orca.android.kotlin.biblelockscreen.permission.PermissionChecker
import com.duke.orca.android.kotlin.biblelockscreen.permission.model.Permission
import com.duke.orca.android.kotlin.biblelockscreen.permission.view.PermissionRationaleDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class MainActivity : BaseLockScreenActivity(), PermissionRationaleDialogFragment.OnPermissionAllowClickListener {
    private val viewModel by viewModels<MainViewModel>()
    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            if (PermissionRationaleDialogFragment.permissionsGranted(this)) {
                startService()
            } else {
                PermissionRationaleDialogFragment().also {
                    it.show(supportFragmentManager, it.tag)
                }
            }
        } else {
            PermissionRationaleDialogFragment().also {
                it.show(supportFragmentManager, it.tag)
            }
        }
    }

    private var handler: Handler? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BehaviourEventBus.newInstance()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            when {
                checkPermission(permission) -> checkManageOverlayPermission()
                shouldShowRequestPermissionRationale(permission) -> PermissionRationaleDialogFragment().also {
                    if (LockScreenService.isRunning.not()) {
                        it.show(supportFragmentManager, it.tag)
                    }
                }
                else -> activityResultLauncher.launch(permission)
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

    override fun onPermissionAllowClick(permission: Permission) {
        when (permission.permission) {
            Manifest.permission.POST_NOTIFICATIONS -> checkPostNotificationsPermission()
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION -> checkManageOverlayPermission()
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

    private fun checkPostNotificationsPermission() {
        if (checkPermission(Manifest.permission.POST_NOTIFICATIONS))
            startService()
        else {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY

                putExtra(EXTRA_APP_PACKAGE, packageName)
            }

            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

            handler = Handler(mainLooper)

            handler?.postDelayed(object : Runnable {
                override fun run() {
                    if (checkPermission(Manifest.permission.POST_NOTIFICATIONS)) {
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