package com.duke.orca.android.kotlin.biblelockscreen.billing.module

import android.content.Context
import androidx.annotation.MainThread
import com.android.billingclient.api.*
import com.duke.orca.android.kotlin.biblelockscreen.application.`is`
import com.duke.orca.android.kotlin.biblelockscreen.billing.REMOVE_ADS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class BillingModule(
    context: Context,
    private val callback: Callback
) {
    interface Callback {
        fun onBillingSetupFinished(billingClient: BillingClient)
        @MainThread
        fun onFailure(responseCode: Int)
        @MainThread
        fun onSuccess(purchase: Purchase)
    }

    private val consumableSkuList = emptyList<String>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        purchases?.let {
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                }
                else -> {
                    callback.onFailure(billingResult.responseCode)
                }
            }
        }
    }

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    private val billingClientStateListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                callback.onBillingSetupFinished(billingClient)
            } else {
                callback.onFailure(billingResult.responseCode)
            }
        }

        override fun onBillingServiceDisconnected() {
        }
    }

    fun startConnection() {
        try {
            billingClient.startConnection(billingClientStateListener)
        } catch (e: IllegalStateException) {
            Timber.e(e)
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        when {
            consumableSkuList.containsAll(purchase.skus) -> {
                val consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                coroutineScope.launch {
                    val consumeResult = billingClient.consumePurchase(consumeParams)

                    withContext(Dispatchers.Main) {
                        if (consumeResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            callback.onSuccess(purchase)
                        }
                    }
                }
            }

            purchase.purchaseState == Purchase.PurchaseState.PURCHASED -> {
                if (purchase.isAcknowledged.not()) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)

                    coroutineScope.launch {
                        val billingResult = billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())

                        withContext(Dispatchers.Main) {
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                callback.onSuccess(purchase)
                            } else {
                                callback.onFailure(billingResult.responseCode)
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val skuList = listOf(REMOVE_ADS)

        suspend fun querySkuDetails(
            billingClient: BillingClient,
            @MainThread onSkuDetailsResult: (SkuDetailsResult) -> Unit
        ) {
            val builder = SkuDetailsParams.newBuilder().apply {
                setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
            }

            withContext(Dispatchers.IO) {
                val skuDetailsResult = billingClient.querySkuDetails(builder.build())

                withContext(Dispatchers.Main) {
                    onSkuDetailsResult.invoke(skuDetailsResult)
                }
            }
        }

        suspend fun isPurchased(billingClient: BillingClient, sku: String): Boolean {
            billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP).purchasesList.let { purchasesList ->
                for (purchase in purchasesList) {
                    if (purchase.skus.contains(sku)) {
                        if (purchase.isAcknowledged and purchase.isPurchased) {
                            return true
                        }
                    }
                }

                return false
            }
        }

        private val Purchase.isPurchased: Boolean
            get() = purchaseState.`is`(Purchase.PurchaseState.PURCHASED)
    }
}