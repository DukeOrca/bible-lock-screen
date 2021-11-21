package com.duke.orca.android.kotlin.biblelockscreen.base.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.duke.orca.android.kotlin.biblelockscreen.R

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

    protected fun addFragment(
        @IdRes containerViewId: Int,
        fragmentManager: FragmentManager,
        fragment: Fragment
    ) {
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .setReorderingAllowed(true)
            .add(containerViewId, fragment, fragment.tag)
            .addToBackStack(fragment.tag)
            .commit()
    }
}