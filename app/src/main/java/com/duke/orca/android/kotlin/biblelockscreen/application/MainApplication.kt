package com.duke.orca.android.kotlin.biblelockscreen.application

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.duke.orca.android.kotlin.biblelockscreen.BuildConfig
import com.duke.orca.android.kotlin.biblelockscreen.billing.REMOVE_ADS
import com.duke.orca.android.kotlin.biblelockscreen.billing.module.BillingModule
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.eventbus.NetworkStatusEventBus
import com.duke.orca.android.kotlin.biblelockscreen.eventbus.RemoveAdsEventBus
import com.duke.orca.android.kotlin.biblelockscreen.networkstatus.NetworkStatus
import com.duke.orca.android.kotlin.biblelockscreen.networkstatus.NetworkStatusTracker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application() {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val billingModule by lazy {
        BillingModule(this, object : BillingModule.Callback {
            override fun onBillingSetupFinished(billingClient: BillingClient) {
                coroutineScope.launch(Dispatchers.IO) {
                    removeAdsEventBus.post(BillingModule.isPurchased(billingClient, REMOVE_ADS))
                }
            }

            override fun onFailure(responseCode: Int) {
            }

            override fun onSuccess(purchase: Purchase) {
            }
        })
    }

    private val networkStatusEventBus = NetworkStatusEventBus.getInstance()
    private val removeAdsEventBus = RemoveAdsEventBus.getInstance()

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        with(DataStore.Display.isDarkMode(this)) {
            AppCompatDelegate.setDefaultNightMode(
                if (this) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        coroutineScope.launch {
            NetworkStatusTracker(applicationContext).networkStatus.collect {
                when(it) {
                    NetworkStatus.Available -> billingModule.startConnection()
                    NetworkStatus.Unavailable -> removeAdsEventBus.post(true)
                }

                networkStatusEventBus.post(it)
            }
        }
    }

    companion object {
        private lateinit var INSTANCE: MainApplication

        val applicationContext: Context
            get() = INSTANCE.applicationContext
    }
}