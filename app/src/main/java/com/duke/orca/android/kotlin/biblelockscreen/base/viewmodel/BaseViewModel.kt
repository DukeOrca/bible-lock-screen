package com.duke.orca.android.kotlin.biblelockscreen.base.viewmodel

import android.app.Application
import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.duke.orca.android.kotlin.biblelockscreen.billing.REMOVE_ADS
import com.duke.orca.android.kotlin.biblelockscreen.billing.module.BillingModule
import com.duke.orca.android.kotlin.biblelockscreen.datastore.dataStore
import com.duke.orca.android.kotlin.biblelockscreen.networkstatus.NetworkStatus
import com.duke.orca.android.kotlin.biblelockscreen.networkstatus.NetworkStatusTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    private val networkStatus = NetworkStatusTracker(application).networkStatus

    val settings = application.dataStore.data.asLiveData(Dispatchers.IO)

    private val _removeAds = MutableLiveData<Boolean>()
    val removeAds: LiveData<Boolean> get() = _removeAds

    private val billingModule = BillingModule(application, object : BillingModule.Callback {
        override fun onBillingSetupFinished(billingClient: BillingClient) {
            viewModelScope.launch(Dispatchers.IO) {
                _removeAds.postValue(BillingModule.isPurchased(billingClient, REMOVE_ADS))
            }
        }

        override fun onFailure(responseCode: Int) {
        }

        override fun onSuccess(purchase: Purchase) {
        }
    })

    init {
        viewModelScope.launch(Dispatchers.IO) {
            networkStatus.collect {
                when(it) {
                    NetworkStatus.Available -> {
                        if (billingModule.billingSetupFinished.get().not()) {
                            billingModule.startConnection()
                        }
                    }
                    NetworkStatus.Unavailable -> {
                        withContext(Dispatchers.Main) {
                            removeAds()
                        }
                    }
                }
            }
        }
    }

    @MainThread
    private fun removeAds() {
        _removeAds.value = true
    }
}