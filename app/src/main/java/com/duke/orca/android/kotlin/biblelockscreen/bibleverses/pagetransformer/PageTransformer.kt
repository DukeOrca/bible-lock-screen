package com.duke.orca.android.kotlin.biblelockscreen.bibleverses.pagetransformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.duke.orca.android.kotlin.biblelockscreen.application.toPx
import kotlin.math.abs

class PageTransformer(private val pageMargin: Float) : ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        view.apply {
            translationX = position * pageMargin
        }
    }
}