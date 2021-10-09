package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodel

import androidx.lifecycle.*
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.repository.BibleChapterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
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

    suspend fun get(book: Int, chapter: Int): BibleChapter {
        return repository.get(book, chapter).first()
    }

    suspend fun getAll() = repository.getAll().first()
}