package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodel

import androidx.lifecycle.*
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.repository.BibleVerseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BibleVerseSearchViewModel @Inject constructor(private val repository: BibleVerseRepository) : ViewModel() {
    private val _searchResult = MutableLiveData<SearchResult>()
    val searchResult: LiveData<SearchResult> = _searchResult

    fun get(id: Int) = repository.get(id).asLiveData(viewModelScope.coroutineContext)

    fun search(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _searchResult.value = SearchResult(repository.search(text).first(), text)
            }
        }
    }

    fun updateFavorites(id: Int, favorites: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFavorites(id, favorites)
        }
    }
}

data class SearchResult(
    val searchResults: List<BibleVerse>,
    val searchWord: String
)