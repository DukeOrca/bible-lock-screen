package com.duke.orca.android.kotlin.biblelockscreen.base.viewmodels

import android.app.Application
import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodel.AttributeSet
import com.duke.orca.android.kotlin.biblelockscreen.billing.REMOVE_ADS
import com.duke.orca.android.kotlin.biblelockscreen.billing.module.BillingModule
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.datastore.PreferencesKeys
import com.duke.orca.android.kotlin.biblelockscreen.datastore.dataStore
import com.duke.orca.android.kotlin.biblelockscreen.eventbus.NetworkStatusEventBus
import com.duke.orca.android.kotlin.biblelockscreen.eventbus.RemoveAdsEventBus
import com.duke.orca.android.kotlin.biblelockscreen.networkstatus.NetworkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    val data = application.dataStore.data.asLiveData(Dispatchers.IO)
    val attributeSet = data.map {
        val bold = it[PreferencesKeys.Font.bold] ?: false
        val fontSize = it[PreferencesKeys.Font.fontSize] ?: DataStore.Font.DEFAULT_FONT_SIZE
        val textAlignment = it[PreferencesKeys.Font.textAlignment] ?: DataStore.Font.TextAlignment.LEFT

        AttributeSet(
            bold = bold,
            fontSize = fontSize,
            textAlignment = textAlignment
        )
    }
}