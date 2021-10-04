package com.duke.orca.android.kotlin.biblelockscreen.bibleverses.viewmodel

import androidx.lifecycle.*
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.repository.BibleVersesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BibleVerseSearchViewModel @Inject constructor(private val repository: BibleVersesRepository) : ViewModel() {
    private val _results = MutableLiveData<List<BibleVerse>>()
    val results: LiveData<List<BibleVerse>> = _results

    fun get(id: Int) = repository.get(id).asLiveData(viewModelScope.coroutineContext)

    fun search(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _results.value = repository.search(text).first()
            }
        }
    }

    fun updateFavorites(id: Int, favorites: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFavorites(id, favorites)
        }
    }
}