package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.duke.orca.android.kotlin.biblelockscreen.base.viewmodel.BaseViewModel
import com.duke.orca.android.kotlin.biblelockscreen.bible.repository.BibleVerseRepository
import com.duke.orca.android.kotlin.biblelockscreen.datastore.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BibleVerseViewModel @Inject constructor(
    private val repository: BibleVerseRepository,
    application: Application
) : BaseViewModel(application) {
    suspend fun get(id: Int) = repository.get(id).flowOn(Dispatchers.IO).first()

    fun updateFavorites(id: Int, favorites: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFavorites(id, favorites)
        }
    }
}