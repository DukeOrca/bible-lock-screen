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

object AdLoader {
    fun loadNativeAd(context: Context, onLoadAd: (NativeAd) -> Unit) {
        val adUnitID = if (BuildConfig.DEBUG) R.string.native_advanced_sample_ad_unit_id else R.string.native_advanced_ad_unit_id
        val adLoader = AdLoader.Builder(context, context.getString(adUnitID))
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

    fun populateNativeAdView(binding: NativeAdBinding, nativeAd: NativeAd) {
        binding.headline.text = nativeAd.headline

        nativeAd.advertiser?.let {
            binding.advertiser.text = it
            binding.advertiser.show()
        } ?: let { binding.advertiser.hide() }

        nativeAd.icon?.let {
            Glide.with(binding.icon)
                .load(it.drawable)
                .transform(CenterCrop(), RoundedCorners(4.toPx))
                .into(binding.icon)
            binding.icon.show()
        } ?: let { binding.icon.hide() }

        nativeAd.callToAction?.let {
            binding.callToAction.text = it
            binding.callToAction.show()
        } ?: let { binding.callToAction.hide() }

        nativeAd.store?.let {
            binding.store.text = it
            binding.store.show()
        } ?: let { binding.store.hide() }

        binding.nativeAdView.callToActionView = binding.callToAction
        binding.nativeAdView.setNativeAd(nativeAd)
    }
}