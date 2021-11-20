package com.duke.orca.android.kotlin.biblelockscreen.settings.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FontSettingsBottomSheetDialogViewModel(application: Application)
    : AndroidViewModel(application) {
    fun putSize(size: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            DataStore.Font.Bible.putSize(getApplication(), size)
        }
    }

    fun putTextAlignment(textAlignment: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            DataStore.Font.Bible.putTextAlignment(getApplication(), textAlignment)
        }
    }
}