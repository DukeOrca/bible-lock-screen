package com.duke.orca.android.kotlin.biblelockscreen.base.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.viewbinding.ViewBinding
import com.duke.orca.android.kotlin.biblelockscreen.billing.listener.RemoveAdsPurchaseStateListener
import com.duke.orca.android.kotlin.biblelockscreen.main.viewmodel.MainViewModel
import kotlinx.coroutines.*

abstract class BaseFragment<VB: ViewBinding> : Fragment() {
    private var _viewBinding: VB? = null
    protected val viewBinding: VB
        get() = _viewBinding!!

    protected val activityViewModel by activityViewModels<MainViewModel>()

    private val activityResultLauncherHashMap = hashMapOf<String, ActivityResultLauncher<Intent>>()

    protected fun putActivityResultLauncher(key: String, value: ActivityResultLauncher<Intent>) {
        activityResultLauncherHashMap[key] = value
    }

    protected fun getActivityResultLauncher(key: String) = activityResultLauncherHashMap[key]

    abstract fun inflate(inflater: LayoutInflater, container: ViewGroup?): VB

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = inflate(inflater, container)

        if (this is RemoveAdsPurchaseStateListener) {
            activityViewModel.removeAds.observe(viewLifecycleOwner, {
                if (it) {
                    removeAdsPurchased()
                } else {
                    removeAdsPending()
                }
            })
        }

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

    protected fun finish() {
        requireActivity().finish()
    }

    protected fun recreate() {
        requireActivity().recreate()
    }
}