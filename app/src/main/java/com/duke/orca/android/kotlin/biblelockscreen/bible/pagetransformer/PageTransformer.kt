package com.duke.orca.android.kotlin.biblelockscreen.bible.pagetransformer

import android.animation.Animator
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.duke.orca.android.kotlin.biblelockscreen.application.Duration
import java.util.concurrent.atomic.AtomicBoolean

class PageTransformer(private val pageMargin: Float, scheduleAnimation: Boolean) : ViewPager2.PageTransformer {
    private val isAnimationScheduled = AtomicBoolean(scheduleAnimation)

    override fun transformPage(view: View, position: Float) {
        view.apply {
            if (isAnimationScheduled.get()) {
                this.animate()
                    .translationX(position * pageMargin)
                    .setDuration(Duration.SHORT)
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            if (position >= 1.0F) {
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