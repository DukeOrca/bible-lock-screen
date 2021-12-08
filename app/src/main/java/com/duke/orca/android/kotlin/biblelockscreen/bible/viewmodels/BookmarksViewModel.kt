package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Position
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.PositionRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.VerseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val positionRepository: PositionRepository,
    private val verseRepository: VerseRepository
) : ViewModel() {
    fun insertPosition(position: Position) = runBlocking {
        positionRepository.insert(position)
    }

    fun updateBookmark(id: Int, bookmark: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            verseRepository.updateBookmark(id, bookmark)
        }
    }

    val bookmarks = verseRepository.loadBookmarks()
}