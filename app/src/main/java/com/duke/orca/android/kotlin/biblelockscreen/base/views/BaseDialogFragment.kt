package com.duke.orca.android.kotlin.biblelockscreen.base.views

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.coroutineScope
import androidx.viewbinding.ViewBinding
import com.duke.orca.android.kotlin.biblelockscreen.R
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment() {
    private var _viewBinding: VB? = null
    protected val viewBinding: VB
        get() = _viewBinding!!

    abstract val setWindowAnimation: Boolean
    abstract fun inflate(inflater: LayoutInflater, container: ViewGroup?): VB

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = inflate(inflater, container)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return viewBinding.root
    }

    @CallSuper
    override fun onDestroyView() {
        _viewBinding = null
        super.onDestroyView()
    }

    protected fun delayOnLifecycle(
        timeMillis: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        block: () -> Unit
    ) {
        viewLifecycleOwner.lifecycle.coroutineScope.launch(dispatcher) {
            delay(timeMillis)
            block()
        }
    }

    protected fun showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(requireContext(), text, duration).show()
    }
}