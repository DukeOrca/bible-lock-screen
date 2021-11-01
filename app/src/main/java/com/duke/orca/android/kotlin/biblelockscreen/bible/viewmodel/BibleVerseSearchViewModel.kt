package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodel

import androidx.lifecycle.*
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleBookRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleVerseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BibleVerseSearchViewModel @Inject constructor(
    private val bibleBookRepository: BibleBookRepository,
    private val bibleVerseRepository: BibleVerseRepository,
) : ViewModel() {
    private val _searchResult = MutableLiveData<SearchResult>()
    val searchResult: LiveData<SearchResult> = _searchResult
    val bibleBook by lazy { bibleBookRepository.get() }

    fun get(id: Int) = bibleVerseRepository.get(id).asLiveData(viewModelScope.coroutineContext)

    fun search(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _searchResult.value = SearchResult(bibleVerseRepository.search(text).first(), text)
            }
        }
    }

    fun updateFavorites(id: Int, favorites: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            bibleVerseRepository.updateFavorites(id, favorites)
        }
    }
}

data class SearchResult(
    val searchResults: List<BibleVerse>,
    val searchWord: String
)