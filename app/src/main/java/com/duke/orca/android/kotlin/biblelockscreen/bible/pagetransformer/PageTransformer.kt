package com.duke.orca.android.kotlin.biblelockscreen.bible.pagetransformer

import android.animation.Animator
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import java.util.concurrent.atomic.AtomicBoolean

class PageTransformer(private val pageMargin: Float, scheduleAnimation: Boolean) : ViewPager2.PageTransformer {
    private val isAnimationScheduled = AtomicBoolean(scheduleAnimation)

    private var pageAnimatorListener: PageAnimatorListener? = null

    interface PageAnimatorListener {
        fun onPageAnimationEnd()
    }

    fun setPageAnimatorListener(pageAnimatorListener: PageAnimatorListener) {
        this.pageAnimatorListener = pageAnimatorListener
    }

    override fun transformPage(page: View, position: Float) {
        page.apply {
            if (isAnimationScheduled.get()) {
               this.animate()
                    .translationX(position * pageMargin)
                    .setDuration(Duration.SHORT)
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            if (position >= 1.0F) {
                                pageAnimatorListener?.onPageAnimationEnd()
                                isAnimationScheduled.set(false)
                            }
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationRepeat(animation: Animator?) {
                        }
                    })
                    .start()
            } else {
                translationX = position * pageMargin
            }
        }
    }
}