package com.duke.orca.android.kotlin.biblelockscreen.main.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.BLANK
import com.duke.orca.android.kotlin.biblelockscreen.application.SingleLiveEvent
import com.duke.orca.android.kotlin.biblelockscreen.application.SystemUiColorAction
import com.duke.orca.android.kotlin.biblelockscreen.billing.REMOVE_ADS
import com.duke.orca.android.kotlin.biblelockscreen.billing.module.BillingModule
import com.duke.orca.android.kotlin.biblelockscreen.datastore.dataStore
import com.duke.orca.android.kotlin.biblelockscreen.networkstatus.NetworkStatus
import com.duke.orca.android.kotlin.biblelockscreen.networkstatus.NetworkStatusTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val networkStatus = NetworkStatusTracker(application).networkStatus

    private val _removeAds = MutableLiveData<Boolean>()
    val removeAds: LiveData<Boolean> get() = _removeAds


    private val billingModule = BillingModule(getApplication(), object : BillingModule.Callback {
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
                        billingModule.startConnection()
                    }
                    NetworkStatus.Unavailable -> {
                    }
                }
            }
        }
    }

    val books: Array<String> = application.resources.getStringArray(R.array.books)
    val chapters = application.resources.getIntArray(R.array.chapters)
    val settings = application.dataStore.data.asLiveData(Dispatchers.IO)

    private val _closeDrawer = SingleLiveEvent<Unit>()
    val closeDrawer: LiveData<Unit> get() = _closeDrawer

    private val _systemUiColorChanged = SingleLiveEvent<SystemUiColorAction>()
    val systemUiColorChanged: LiveData<SystemUiColorAction> = _systemUiColorChanged

    fun getBook(book: Int): String {
        return if (book.dec() in 0 until books.count()) {
            books[book.dec()]
        } else {
            BLANK
        }
    }

    fun setSystemUiColor() {
        _systemUiColorChanged.value = SystemUiColorAction.SET
    }

    fun revertSystemUiColor() {
        _systemUiColorChanged.value = SystemUiColorAction.REVERT
    }

    fun callCloseDrawer() {
        _closeDrawer.call()
    }
}