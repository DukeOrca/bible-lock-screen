package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import androidx.lifecycle.ViewModel
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleBookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookSelectionDialogViewModel @Inject constructor(
    bibleBookRepository: BibleBookRepository,
) : ViewModel() {
    val book = bibleBookRepository.get()
}