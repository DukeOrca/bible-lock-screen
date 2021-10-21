package com.duke.orca.android.kotlin.biblelockscreen.admob

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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
import com.google.android.gms.ads.nativead.NativeAdOptions
import timber.log.Timber

object AdLoader {
    fun loadNativeAd(context: Context, onLoadAd: (NativeAd) -> Unit) {
        val adLoader = AdLoader.Builder(context, context.getString(R.string.native_advanced_ad_unit_id))
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
        viewBinding.headline.text = nativeAd.headline

        nativeAd.advertiser?.let {
            viewBinding.advertiser.text = it
            viewBinding.advertiser.show()
        } ?: let { viewBinding.advertiser.hide() }

        nativeAd.icon?.let {
            Glide.with(viewBinding.icon)
                .load(it.drawable)
                .transform(CenterCrop(), RoundedCorners(4.toPx))
                .into(viewBinding.icon)
            viewBinding.icon.show()
        } ?: let { viewBinding.icon.hide() }

        nativeAd.callToAction?.let {
            viewBinding.callToAction.text = it
            viewBinding.callToAction.show()
        } ?: let { viewBinding.callToAction.hide() }

        nativeAd.store?.let {
            viewBinding.store.text = it
            viewBinding.store.show()
        } ?: let { viewBinding.store.hide() }

        viewBinding.nativeAdView.callToActionView = viewBinding.callToAction
        viewBinding.nativeAdView.setNativeAd(nativeAd)
    }
}