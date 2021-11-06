package com.duke.orca.android.kotlin.biblelockscreen.eventbus

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import timber.log.Timber

class RemoveAdsEventBus private constructor() {
    private val behaviorSubject by lazy {
        BehaviorSubject.create<Boolean>()
    }

    fun post(value: Boolean) {
        behaviorSubject.onNext(value)
    }

    fun subscribe(subscribe: (Boolean) -> Unit): Disposable {
        return behaviorSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                subscribe.invoke(it)
            }) {
                Timber.e(it)
            }
    }

    companion object {
        private var INSTANCE: RemoveAdsEventBus? = null

        fun getInstance(): RemoveAdsEventBus {
            return INSTANCE ?: RemoveAdsEventBus().also {
                INSTANCE = it
            }
        }
    }
}