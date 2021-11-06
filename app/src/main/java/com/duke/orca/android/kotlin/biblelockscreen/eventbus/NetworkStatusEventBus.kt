package com.duke.orca.android.kotlin.biblelockscreen.eventbus

import com.duke.orca.android.kotlin.biblelockscreen.networkstatus.NetworkStatus
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import timber.log.Timber

class NetworkStatusEventBus private constructor() {
    private val behaviorSubject by lazy {
        BehaviorSubject.create<NetworkStatus>()
    }

    fun post(value: NetworkStatus) {
        behaviorSubject.onNext(value)
    }

    fun subscribe(subscribe: (NetworkStatus) -> Unit): Disposable {
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
        private var INSTANCE: NetworkStatusEventBus? = null

        fun getInstance(): NetworkStatusEventBus {
            return INSTANCE ?: NetworkStatusEventBus().also {
                INSTANCE = it
            }
        }
    }
}