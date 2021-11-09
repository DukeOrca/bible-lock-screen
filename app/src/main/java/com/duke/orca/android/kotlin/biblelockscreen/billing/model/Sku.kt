package com.duke.orca.android.kotlin.biblelockscreen.billing.model

sealed class Sku {
    abstract val isPurchased: Boolean

    class RemoveAds(
        override val isPurchased: Boolean
    ) : Sku()
}