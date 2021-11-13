package com.duke.orca.android.kotlin.biblelockscreen.base.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.annotation.LayoutRes
import androidx.lifecycle.lifecycleScope
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeOut
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentViewStubBinding
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseViewStubFragment : BaseFragment<FragmentViewStubBinding>() {
    @get:LayoutRes
    abstract val layoutResource: Int

    private var viewStub: ViewStub? = null
    private var onResumed = AtomicBoolean(false)

    protected var isInflated = AtomicBoolean(false)

    abstract fun onInflated(view: View)

    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentViewStubBinding {
        return FragmentViewStubBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        viewStub = viewBinding.root.findViewById(R.id.view_stub)
        viewStub?.layoutResource = layoutResource
        inflate()

        return viewBinding.root
    }

    override fun onResume() {
        super.onResume()
        onResumed.set(true)
        inflate()
    }

    override fun onPause() {
        onResumed.set(false)
        super.onPause()
    }

    private fun inflate() {
        if (onResumed.get() and isInflated.get().not()) {
            try {
                viewStub?.inflate()?.let {
                    viewBinding.circularProgressIndicator.fadeOut(Duration.FADE_OUT) {
                        delayOnLifecycle(Duration.Delay.SHORT) {
                            onInflated(it)
                            afterOnInflated()
                        }
                    }
                }
            } catch (e: IllegalStateException) {
                Timber.e(e)
            }
        }
    }

    private fun afterOnInflated() {
        isInflated.set(true)
    }
}