package com.duke.orca.android.kotlin.biblelockscreen.admob

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.duke.orca.android.kotlin.biblelockscreen.BuildConfig
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.toPx
import com.duke.orca.android.kotlin.biblelockscreen.application.hide
import com.duke.orca.android.kotlin.biblelockscreen.application.show
import com.duke.orca.android.kotlin.biblelockscreen.databinding.NativeAdBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import timber.log.Timber
import java.lang.ref.WeakReference

object AdLoader {
    fun loadNativeAd(context: Context, onLoadAd: (NativeAd) -> Unit) {
        val applicationContext = context.applicationContext
        val adUnitID = if (BuildConfig.DEBUG) R.string.native_advanced_sample_ad_unit_id else R.string.native_advanced_ad_unit_id
        val adLoader = AdLoader.Builder(
            applicationContext,
            applicationContext.getString(adUnitID)
        )
            .forNativeAd { nativeAd ->
                onLoadAd(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Timber.e(loadAdError.message)
                }
            })
            .build()

        val adRequest = AdRequest.Builder().build()

        adLoader.loadAd(adRequest)
    }

    fun populateNativeAdView(weakReference: WeakReference<NativeAdBinding>, nativeAd: NativeAd) {
        val nativeAdBinding = weakReference.get() ?: return

        nativeAdBinding.headline.text = nativeAd.headline

        nativeAd.advertiser?.let {
            nativeAdBinding.advertiser.text = it
            nativeAdBinding.advertiser.show()
        } ?: let { nativeAdBinding.advertiser.hide() }

        nativeAd.icon?.let {
            Glide.with(nativeAdBinding.icon)
                .load(it.drawable)
                .transform(CenterCrop(), RoundedCorners(4.toPx))
                .into(nativeAdBinding.icon)
            nativeAdBinding.icon.show()
        } ?: let { nativeAdBinding.icon.hide() }

        nativeAd.callToAction?.let {
            nativeAdBinding.callToAction.text = it
            nativeAdBinding.callToAction.show()
        } ?: let { nativeAdBinding.callToAction.hide() }

        nativeAd.store?.let {
            nativeAdBinding.store.text = it
            nativeAdBinding.store.show()
        } ?: let { nativeAdBinding.store.hide() }

        nativeAdBinding.nativeAdView.callToActionView = nativeAdBinding.callToAction
        nativeAdBinding.nativeAdView.setNativeAd(nativeAd)
    }
}