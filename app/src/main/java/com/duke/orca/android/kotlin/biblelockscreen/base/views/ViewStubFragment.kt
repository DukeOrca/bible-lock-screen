package com.duke.orca.android.kotlin.biblelockscreen.base.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeOut
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentViewStubBinding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

abstract class ViewStubFragment : Fragment() {
    @get:LayoutRes
    abstract val layoutResource: Int

    private val isInflated = AtomicBoolean(false)
    private val onResumed = AtomicBoolean(false)

    private var _viewStubBinding: FragmentViewStubBinding? = null
    protected val viewStubBinding: FragmentViewStubBinding
        get() = _viewStubBinding!!

    private var viewStub: ViewStub? = null

    abstract fun onInflate(view: View)

    private fun inflate(
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

        _viewStubBinding = inflate(inflater, container)

        viewStub = viewStubBinding.viewStub
        viewStub?.layoutResource = layoutResource

        inflate()

        return viewStubBinding.root
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

    override fun onDestroyView() {
        _viewStubBinding = null
        super.onDestroyView()
    }

    private fun inflate() {
        if (onResumed.get() and isInflated.get().not()) {
            try {
                viewStub?.inflate()?.let {
                    viewStubBinding.circularProgressIndicator.fadeOut(Duration.FADE_OUT) {
                        delayOnLifecycle(Duration.Delay.SHORT) {
                            onInflate(it)
                            isInflated.set(true)
                        }
                    }
                }
            } catch (e: IllegalStateException) {
                Timber.e(e)
            }
        }
    }

    protected fun delayOnLifecycle(
        timeMillis: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        block: () -> Unit
    ) {
        try {
            viewLifecycleOwner.lifecycle.coroutineScope.launch(dispatcher) {
                delay(timeMillis)
                block()
            }
        } catch (e: IllegalStateException) {
            Timber.e(e)
        }
    }
}