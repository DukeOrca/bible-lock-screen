package com.duke.orca.android.kotlin.biblelockscreen.bibleverses.viewmodel

import androidx.lifecycle.*
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.repository.BibleVersesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BibleVersePagerViewModel @Inject constructor(private val repository: BibleVersesRepository) : ViewModel() {
    private val _currentItem = MutableLiveData<BibleVerse>()
    val currentItem: LiveData<BibleVerse> = _currentItem

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