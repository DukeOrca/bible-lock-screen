package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Position
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Verse
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.PositionRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.VerseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HighlightsViewModel @Inject constructor(
    private val verseRepository: VerseRepository,
    private val positionRepository: PositionRepository,
    application: Application
) : AndroidViewModel(application) {
    val highlights = verseRepository.loadHighlights()

    fun insertPosition(position: Position) = runBlocking {
        positionRepository.insert(position)
    }
}