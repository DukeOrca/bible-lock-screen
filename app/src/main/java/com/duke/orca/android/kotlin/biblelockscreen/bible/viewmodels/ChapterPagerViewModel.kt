package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Position
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.PositionRepository
import com.duke.orca.android.kotlin.biblelockscreen.datastore.recentlyReadDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class ChapterPagerViewModel (application: Application) : AndroidViewModel(application) {
    private val _currentChapter = MutableLiveData<Int>()
    val currentChapter: LiveData<Int>
        get() = _currentChapter

    private val positionRepository: PositionRepository
        get() = PositionRepository.from(getApplication())

    val recentlyReadDataStore = application.recentlyReadDataStore

    fun insertPosition(position: Position) = runBlocking {
        if (position.value < 0) return@runBlocking

        positionRepository.insert(position)
    }

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