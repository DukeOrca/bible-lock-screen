package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import androidx.lifecycle.ViewModel
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookSelectionDialogViewModel @Inject constructor(
    bookRepository: BookRepository,
) : ViewModel() {
    val book = bookRepository.get()
}