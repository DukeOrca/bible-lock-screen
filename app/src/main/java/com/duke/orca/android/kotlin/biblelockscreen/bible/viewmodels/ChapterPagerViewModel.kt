package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BookRepository
import com.duke.orca.android.kotlin.biblelockscreen.datastore.PreferencesKeys
import com.duke.orca.android.kotlin.biblelockscreen.datastore.preferencesDataStore
import com.duke.orca.android.kotlin.biblelockscreen.datastore.recentlyReadDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ChapterPagerViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    application: Application
) : AndroidViewModel(application) {
    private val _currentChapter = MutableLiveData<Int>()
    val currentChapter: LiveData<Int>
        get() = _currentChapter

    val bibleBook by lazy { bookRepository.get() } // todo 확인필.

    val recentlyReadDataStore = application.recentlyReadDataStore

    fun setCurrentChapter(value: Int) {
        _currentChapter.value = value
    }

    fun updateRecentlyRead(book: Int, chapter: Int) = runBlocking {
        recentlyReadDataStore.updateData {
            it.toBuilder()
                .setBook(book)
                .setChapter(chapter)
                .build()
        }
    }
}