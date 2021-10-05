package com.duke.orca.android.kotlin.biblelockscreen.review

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

object Review {
    fun launchReviewFlow(activity: Activity) {
        val reviewManager = ReviewManagerFactory.create(activity)
        val task = reviewManager.requestReviewFlow()

        task.addOnCompleteListener {
            if (it.isSuccessful) {
                val reviewInfo = it.result

                reviewInfo.describeContents()
                reviewManager.launchReviewFlow(activity, reviewInfo).apply {
                    addOnCompleteListener {
                        CoroutineScope(Dispatchers.Main).launch {
                            activity.getString(R.string.review_000).run {
                                Toast.makeText(activity, this, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            } else {
                Timber.e(task.exception)
                goToPlayStore(activity)
            }
        }
    }

    private fun goToPlayStore(context: Context) {
        try {
            context.startActivity(
                Intent (
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${context.packageName}"))
            )
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent (
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                )
            )
        }
    }
}