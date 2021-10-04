package com.duke.orca.android.kotlin.biblelockscreen.admob

import android.content.Context
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.hide
import com.duke.orca.android.kotlin.biblelockscreen.application.show
import com.duke.orca.android.kotlin.biblelockscreen.databinding.NativeAdBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import timber.log.Timber

object AdLoader {
    fun loadNativeAd(context: Context, onLoadAd: (NativeAd) -> Unit) {
        val adLoader = AdLoader.Builder(context, context.getString(R.string.native_advanced_sample_ad_unit_id))
            .forNativeAd { nativeAd ->
                onLoadAd(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Timber.e(loadAdError.message)
                }
            })
            .withNativeAdOptions(
                NativeAdOptions
                .Builder()
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                .build()
            ).build()

        val adRequest = AdRequest.Builder().build()

        adLoader.loadAd(adRequest)
    }

    fun populateNativeAdView(viewBinding: NativeAdBinding, nativeAd: NativeAd) {
        nativeAd.icon?.let {
            viewBinding.icon.show()
            viewBinding.icon.setImageDrawable(it.drawable)
        } ?: let { viewBinding.icon.hide() }

        nativeAd.advertiser?.let {
            viewBinding.advertiser.text = it
            viewBinding.advertiser.show()
        } ?: let { viewBinding.advertiser.hide(false) }

        nativeAd.body?.let {
            viewBinding.body.text = it
            viewBinding.body.show()
        } ?: let { viewBinding.body.hide(true) }

        nativeAd.callToAction?.let {
            viewBinding.callToAction.text = it
            viewBinding.callToAction.show()
        } ?: let { viewBinding.callToAction.hide(true) }

        viewBinding.headline.text = nativeAd.headline

        viewBinding.nativeAdView.callToActionView = viewBinding.callToAction
        viewBinding.nativeAdView.setNativeAd(nativeAd)
    }
}