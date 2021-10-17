package com.duke.orca.android.kotlin.biblelockscreen.base.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.CallSuper
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding

abstract class BaseChildFragment<VB: ViewBinding> : BaseFragment<VB>() {
    abstract val toolbar: Toolbar?

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        toolbar?.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        return viewBinding.root
    }
}