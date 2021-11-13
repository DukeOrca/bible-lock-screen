package com.duke.orca.android.kotlin.biblelockscreen.base.viewmodels

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.subjects.PublishSubject

class FragmentContainerViewModel(application: Application) : AndroidViewModel(application) {
    private val _activityResult = MutableLiveData<ActivityResult>()
    val activityResult: LiveData<ActivityResult>
        get() = _activityResult

    fun setResult(resultCode: Int, data: Intent) {
        _activityResult.value = ActivityResult(resultCode, data)
    }
}

data class ActivityResult(val resultCode: Int, val data: Intent)