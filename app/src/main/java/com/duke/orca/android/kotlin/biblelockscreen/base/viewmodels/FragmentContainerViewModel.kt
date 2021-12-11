package com.duke.orca.android.kotlin.biblelockscreen.base.viewmodels

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import io.reactivex.rxjava3.subjects.PublishSubject

class FragmentContainerViewModel(application: Application) : AndroidViewModel(application) {
    private val publishSubject = PublishSubject.create<ActivityResult>()

    val activityResult = publishSubject.ofType(ActivityResult::class.java)

    fun setResult(resultCode: Int, data: Intent) {
        publishSubject.onNext(ActivityResult(resultCode, data))
    }
}

data class ActivityResult(val resultCode: Int, val data: Intent)