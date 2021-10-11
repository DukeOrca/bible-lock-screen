package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodel

import androidx.lifecycle.*
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.repository.BibleChapterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BibleChapterPagerViewModel @Inject constructor(private val repository: BibleChapterRepository) : ViewModel() {
    private val _currentItem = MutableLiveData<BibleChapter>()
    val currentItem: LiveData<BibleChapter> = _currentItem

    fun get(id: Int) {
        viewModelScope.launch {
            _currentItem.value = repository.get(id).first()
        }
    }

    fun getBookmarks() = repository.getBookmarks().asLiveData(viewModelScope.coroutineContext)

    fun updateBookmark(id: Int, bookmark: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateBookmark(id, bookmark)
        }
    }

    suspend fun get(book: Int, chapter: Int): BibleChapter {
        return repository.get(book, chapter).first()
    }

    suspend fun getAll() = repository.getAll().flowOn(Dispatchers.IO).first()
}