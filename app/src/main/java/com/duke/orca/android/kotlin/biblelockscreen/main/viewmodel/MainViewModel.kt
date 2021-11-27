package com.duke.orca.android.kotlin.biblelockscreen.main.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.duke.orca.android.kotlin.biblelockscreen.billing.REMOVE_ADS
import com.duke.orca.android.kotlin.biblelockscreen.billing.model.Sku
import com.duke.orca.android.kotlin.biblelockscreen.billing.module.BillingModule
import com.duke.orca.android.kotlin.biblelockscreen.eventbus.BehaviourEventBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var job: Job? = null

    override fun onCleared() {
        job?.cancel()
        super.onCleared()
    }

    val billingModule by lazy {
        BillingModule(getApplication(), object : BillingModule.Callback {
            override fun onBillingSetupFinished(billingClient: BillingClient) {
                job = viewModelScope.launch(Dispatchers.IO) {
                    BehaviourEventBus.post(
                        Sku.RemoveAds(
                            BillingModule.isPurchased(
                                billingClient,
                                REMOVE_ADS
                            )
                        )
                    )
                }
            }

            override fun onFailure(responseCode: Int) {
            }

            override fun onSuccess(purchase: Purchase) {
            }
        })
    }
}