package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.VerseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HighlightsViewModel @Inject constructor(
    private val verseRepository: VerseRepository,
    application: Application
) : AndroidViewModel(application) {
    val highlights = verseRepository.loadHighlights()
}