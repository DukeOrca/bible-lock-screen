package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.duke.orca.android.kotlin.biblelockscreen.base.viewmodel.BaseViewModel
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.repository.BibleVerseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BibleVersePagerViewModel @Inject constructor(
    private val repository: BibleVerseRepository,
    application: Application
) : BaseViewModel(application) {
    private val _currentItem = MutableLiveData<BibleVerse>()
    val currentItem: LiveData<BibleVerse> = _currentItem

    var newState: Int? = null

    fun get(id: Int) {
       viewModelScope.launch {
            _currentItem.value = repository.get(id).first()
        }
    }

    suspend fun getVerseCount(book: Int, chapter: Int): Int {
        return repository.getVerseCount(book, chapter)
    }

    suspend fun get(book: Int, chapter: Int, verse: Int): BibleVerse {
        return repository.get(book, chapter, verse).first()
    }
}