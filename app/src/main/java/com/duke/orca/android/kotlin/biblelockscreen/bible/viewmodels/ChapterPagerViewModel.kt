package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BookRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.ChapterRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChapterPagerViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    application: Application
) : AndroidViewModel(application) {
    private var chapterRepository = ChapterRepositoryImpl.from(application)

    private val _currentItem = MutableLiveData<BibleChapter>()
    val currentItem: LiveData<BibleChapter> = _currentItem
    val bibleBook by lazy { bookRepository.get() } // todo 확인필.

    fun get(id: Int) {
        viewModelScope.launch {
            _currentItem.value = chapterRepository.get(id).first()
        }
    }

    fun getBookmarks() = chapterRepository.getBookmarks().asLiveData(viewModelScope.coroutineContext)

    fun refresh() {
        chapterRepository = ChapterRepositoryImpl.from(getApplication())
    }

    fun updateBookmark(id: Int, bookmark: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            chapterRepository.updateBookmark(id, bookmark)
        }
    }

    suspend fun get(book: Int, chapter: Int): BibleChapter {
        return chapterRepository.get(book, chapter).first()
    }

    suspend fun getAll() = chapterRepository.getAll().flowOn(Dispatchers.IO).first()
}