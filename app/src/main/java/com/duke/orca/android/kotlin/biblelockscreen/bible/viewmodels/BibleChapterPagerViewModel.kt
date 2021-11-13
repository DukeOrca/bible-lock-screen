package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import androidx.lifecycle.*
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleBookRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleChapterRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.SubBookRepository
import com.duke.orca.android.kotlin.biblelockscreen.datastore.PreferencesKeys
import com.duke.orca.android.kotlin.biblelockscreen.datastore.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BibleChapterPagerViewModel @Inject constructor(
    private val bibleBookRepository: BibleBookRepository,
    private val bibleChapterRepository: BibleChapterRepository,
    private val subBookRepository: SubBookRepository
) : ViewModel() {
    private val _currentItem = MutableLiveData<BibleChapter>()
    val currentItem: LiveData<BibleChapter> = _currentItem
    val bibleBook by lazy { bibleBookRepository.get() } // todo 확인필.

    fun get(id: Int) {
        viewModelScope.launch {
            _currentItem.value = bibleChapterRepository.get(id).first()
        }
    }

    fun getBookmarks() = bibleChapterRepository.getBookmarks().asLiveData(viewModelScope.coroutineContext)

    fun updateBookmark(id: Int, bookmark: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            bibleChapterRepository.updateBookmark(id, bookmark)
        }
    }

    suspend fun get(book: Int, chapter: Int): BibleChapter {
        return bibleChapterRepository.get(book, chapter).first()
    }

    suspend fun getAll() = bibleChapterRepository.getAll().flowOn(Dispatchers.IO).first()
}