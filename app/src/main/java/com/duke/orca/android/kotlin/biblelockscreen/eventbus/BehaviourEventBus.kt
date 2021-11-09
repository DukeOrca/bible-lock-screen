package com.duke.orca.android.kotlin.biblelockscreen.eventbus

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import timber.log.Timber

class BehaviourEventBus private constructor() {
    private val behaviorSubject by lazy {
        BehaviorSubject.create<Any>()
    }

    fun post(value: Any) {
        behaviorSubject.onNext(value)
    }

    fun <T> subscribe(clazz: Class<T>, subscribe: (T) -> Unit): Disposable {
        return behaviorSubject
            .ofType(clazz)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                subscribe.invoke(it)
            }) {
                Timber.e(it)
            }
    }

    companion object {
        private var INSTANCE: BehaviourEventBus? = null

        fun getInstance(): BehaviourEventBus {
            return INSTANCE ?: BehaviourEventBus().also {
                INSTANCE = it
            }
        }
    }
}