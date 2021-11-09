package com.duke.orca.android.kotlin.biblelockscreen.bible.unlock

import android.view.MotionEvent
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.toPx
import com.duke.orca.android.kotlin.biblelockscreen.application.hideRipple
import com.duke.orca.android.kotlin.biblelockscreen.application.scale
import com.duke.orca.android.kotlin.biblelockscreen.application.showRipple
import com.duke.orca.android.kotlin.biblelockscreen.application.translateX
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentBibleVersePagerBinding
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class UnlockController(
    private val viewBinding: FragmentBibleVersePagerBinding,
    private val callback: Callback
) {
    interface Callback {
        fun onRestored()
        fun onOutOfRanged()
    }

    private val isOutOfRanged = AtomicBoolean(false)
    private val range = 600.0F

    private var x = 0.0F
    private var y = 0.0F

    fun init() {
        viewBinding.constraintLayoutUnlock.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    disableUserInput()
                    x = event.x
                    y = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    viewBinding.frameLayoutUnlock.showRipple()

                    val distance = sqrt((x - event.x).pow(2) + (y - event.y).pow(2))
                    var scale = abs(range - distance * 0.4F) / range

                    when {
                        scale >= 1.0F -> scale = 1.0F
                        scale < 0.8F -> scale = 0.8F
                    }

                    val alpha = (scale - 0.8F) * 5.0F

                    setAlpha(alpha)
                    setScale(scale)

                    viewBinding.viewPreviousFake.translationX = ((1.0F - alpha) * -8.0F).toPx
                    viewBinding.viewNextFake.translationX = ((1.0F - alpha) * 8.0F).toPx

                    isOutOfRanged.set(distance * 1.25F > range * 0.75F)
                }
                MotionEvent.ACTION_UP -> {
                    if (isOutOfRanged.get()) {
                        callback.onOutOfRanged()
                    } else {
                        restore()
                    }
                }
            }

            true
        }
    }

    private fun setAlpha(alpha: Float) {
        viewBinding.imageViewUnlock.alpha = alpha
        viewBinding.linearLayout.alpha = alpha
        viewBinding.nativeAd.root.alpha = alpha
        viewBinding.viewPreviousFake.alpha = alpha
        viewBinding.viewNextFake.alpha = alpha
    }

    private fun setScale(scale: Float) {
        viewBinding.linearLayout.scaleX = scale
        viewBinding.linearLayout.scaleY = scale
        viewBinding.imageViewUnlock.scaleX = scale
        viewBinding.imageViewUnlock.scaleY = scale
    }

    fun restore() {
        viewBinding.frameLayoutUnlock.hideRipple()
        viewBinding.imageViewUnlock.scale(1.0F, duration = Duration.MEDIUM)
        viewBinding.linearLayout.scale(1.0F, duration =  Duration.MEDIUM)
        viewBinding.viewPreviousFake.translateX(0.0F, duration = Duration.MEDIUM)
        viewBinding.viewNextFake.translateX(0.0F, duration = Duration.MEDIUM) {
            enableUserInput()
        }

        callback.onRestored()
    }

    private fun disableUserInput() {
        viewBinding.viewPager2.isUserInputEnabled = false
    }

    private fun enableUserInput() {
        viewBinding.viewPager2.isUserInputEnabled = true
    }
}