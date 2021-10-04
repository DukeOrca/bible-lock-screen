package com.duke.orca.android.kotlin.biblelockscreen.base.views

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.viewbinding.ViewBinding

abstract class BaseChildFragment<VB: ViewBinding> : BaseFragment<VB>() {
    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return AnimationUtils.loadAnimation(requireContext(), nextAnim).apply {
            setAnimationListener( object: Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (enter) {
                        activityViewModel.setSystemUiColor()
                        activityViewModel.callCloseDrawer()
                    }
                }

                override fun onAnimationStart(animation: Animation?) {
                    if (enter.not()) {
                        activityViewModel.revertSystemUiColor()
                    }
                }
            })
        }
    }
}