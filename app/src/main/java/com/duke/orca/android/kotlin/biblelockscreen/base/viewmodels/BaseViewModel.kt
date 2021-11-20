package com.duke.orca.android.kotlin.biblelockscreen.base.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels.AttributeSet
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.datastore.PreferencesKeys
import com.duke.orca.android.kotlin.biblelockscreen.datastore.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    val data = application.dataStore.data.asLiveData(Dispatchers.IO)
    val attributeSet = data.map {
        val bold = it[PreferencesKeys.Font.bold] ?: false
        val fontSize = it[PreferencesKeys.Font.size] ?: DataStore.Font.DEFAULT_FONT_SIZE
        val textAlignment = it[PreferencesKeys.Font.textAlignment] ?: DataStore.Font.TextAlignment.LEFT

        AttributeSet(
            bold = bold,
            fontSize = fontSize,
            textAlignment = textAlignment
        )
    }
}