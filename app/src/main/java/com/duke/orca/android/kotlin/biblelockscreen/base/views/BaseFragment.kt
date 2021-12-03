package com.duke.orca.android.kotlin.biblelockscreen.base.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.viewbinding.ViewBinding
import com.duke.orca.android.kotlin.biblelockscreen.R
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.*
import timber.log.Timber

abstract class BaseFragment<VB: ViewBinding> : Fragment() {
    private var _viewBinding: VB? = null
    protected val viewBinding: VB
        get() = _viewBinding!!

    private val activityResultLauncherHashMap = hashMapOf<String, ActivityResultLauncher<Intent>>()
    private val behaviourSubjectHashMap = hashMapOf<String, BehaviorSubject<Any>>()
    private val publishSubjectHashMap = hashMapOf<String, PublishSubject<Any>>()

    protected val compositeDisposable = CompositeDisposable()

    val chapters: IntArray by lazy { resources.getIntArray(R.array.chapters) }

    protected fun putActivityResultLauncher(key: String, onActivityResult: (ActivityResult) -> Unit) {
        activityResultLauncherHashMap[key] = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onActivityResult(it)
        }
    }

    protected fun getActivityResultLauncher(key: String) = activityResultLauncherHashMap[key]

    protected fun putPublishSubject(key: String, publishSubject: PublishSubject<Any>) {
        publishSubjectHashMap[key] = publishSubject
    }

    protected fun getPublishSubject(key: String) = publishSubjectHashMap[key]

    protected fun putBehaviourSubject(key: String, behaviorSubject: BehaviorSubject<Any>) {
        behaviourSubjectHashMap[key] = behaviorSubject
    }

    protected fun getBehaviourSubject(key: String) = behaviourSubjectHashMap[key]

    abstract fun inflate(inflater: LayoutInflater, container: ViewGroup?): VB

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = inflate(inflater, container)

        return viewBinding.root
    }

    @CallSuper
    override fun onDestroyView() {
        _viewBinding = null
        compositeDisposable.dispose()
        super.onDestroyView()
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

    protected fun finish() {
        requireActivity().finish()
    }

    protected fun overridePendingTransition(enterAnim: Int, exitAnim: Int) {
        requireActivity().overridePendingTransition(enterAnim, exitAnim)
    }
}