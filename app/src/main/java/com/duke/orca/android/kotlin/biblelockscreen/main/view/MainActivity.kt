package com.duke.orca.android.kotlin.biblelockscreen.main.view

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseLockScreenActivity
import com.duke.orca.android.kotlin.biblelockscreen.billing.REMOVE_ADS
import com.duke.orca.android.kotlin.biblelockscreen.billing.model.Sku
import com.duke.orca.android.kotlin.biblelockscreen.billing.module.BillingModule
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.eventbus.BehaviourEventBus
import com.duke.orca.android.kotlin.biblelockscreen.lockscreen.service.LockScreenService
import com.duke.orca.android.kotlin.biblelockscreen.networkstatus.NetworkStatus
import com.duke.orca.android.kotlin.biblelockscreen.networkstatus.NetworkStatusTracker
import com.duke.orca.android.kotlin.biblelockscreen.permission.PermissionChecker
import com.duke.orca.android.kotlin.biblelockscreen.permission.view.PermissionRationaleDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MainActivity : BaseLockScreenActivity(), PermissionRationaleDialogFragment.OnPermissionAllowClickListener {
    private val behaviourEventBus = BehaviourEventBus.getInstance()
    private val billingModule by lazy {
        BillingModule(this, object : BillingModule.Callback {
            override fun onBillingSetupFinished(billingClient: BillingClient) {
                lifecycleScope.launch(Dispatchers.IO) {
                    behaviourEventBus.post(Sku.RemoveAds(BillingModule.isPurchased(billingClient, REMOVE_ADS)))
                }
            }

            override fun onFailure(responseCode: Int) {
            }

            override fun onSuccess(purchase: Purchase) {
            }
        })
    }

    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (PermissionRationaleDialogFragment.permissionsGranted(this).not()) {
            PermissionRationaleDialogFragment().also {
                it.show(supportFragmentManager, it.tag)
            }
        }

        lifecycleScope.launch {
            NetworkStatusTracker(applicationContext).networkStatus.collect {
                when(it) {
                    NetworkStatus.Available -> billingModule.startConnection()
                    NetworkStatus.Unavailable -> {
                    }
                }

                behaviourEventBus.post(it)
            }
        }

        startService()
    }

    override fun onStart() {
        super.onStart()

        if (DataStore.isFirstTime(this)) {
            DataStore.putFirstTime(this, false)
        }
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
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