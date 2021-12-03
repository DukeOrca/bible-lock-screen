package com.duke.orca.android.kotlin.biblelockscreen.base.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Font
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.datastore.PreferencesKeys
import com.duke.orca.android.kotlin.biblelockscreen.datastore.preferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    val data = application.preferencesDataStore.data.asLiveData(Dispatchers.IO)
    val font = data.map {
        val bold = it[PreferencesKeys.Font.bold] ?: false
        val size = it[PreferencesKeys.Font.size] ?: DataStore.Font.DEFAULT_SIZE
        val textAlignment = it[PreferencesKeys.Font.textAlignment] ?: DataStore.Font.TextAlignment.LEFT

        Font(
            bold = bold,
            size = size,
            textAlignment = textAlignment
        )
    }
}