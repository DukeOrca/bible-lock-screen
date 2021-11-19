package com.duke.orca.android.kotlin.biblelockscreen.settings.viewmodels

import androidx.lifecycle.ViewModel
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleVerseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class FontSettingsBottomSheetDialogViewModel @Inject constructor(
    private val verseRepository: BibleVerseRepository
) : ViewModel() {
    suspend fun verse(id: Int) = verseRepository.get(id).first()
}