package com.duke.orca.android.kotlin.biblelockscreen.base.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.viewbinding.ViewBinding
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.BLANK
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseFragment<VB: ViewBinding> : Fragment() {
    private var _viewBinding: VB? = null
    protected val viewBinding: VB
        get() = _viewBinding!!

    private val activityResultLauncherHashMap = hashMapOf<String, ActivityResultLauncher<Intent>>()
    private val publishSubjectHashMap = hashMapOf<String, PublishSubject<Any>>()

    protected val compositeDisposable = CompositeDisposable()

    val books: Array<String> by lazy { resources.getStringArray(R.array.books) }
    val chapters: IntArray by lazy { resources.getIntArray(R.array.chapters) }

    protected fun putActivityResultLauncher(key: String, value: ActivityResultLauncher<Intent>) {
        activityResultLauncherHashMap[key] = value
    }

    protected fun putPublishSubject(key: String, value: PublishSubject<Any>) {
        publishSubjectHashMap[key] = value
    }

    protected fun getActivityResultLauncher(key: String) = activityResultLauncherHashMap[key]

    protected fun getPublishSubject(key: String): PublishSubject<Any> {
        return publishSubjectHashMap[key] ?: PublishSubject.create<Any>().apply {
            publishSubjectHashMap[key] = this
        }
    }

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

    protected fun getBook(book: Int): String {
        return if (book.dec() in 0 until books.count()) {
            books[book.dec()]
        } else {
            BLANK
        }
    }

    protected fun overridePendingTransition(enterAnim: Int, exitAnim: Int) {
        requireActivity().overridePendingTransition(enterAnim, exitAnim)
    }
}