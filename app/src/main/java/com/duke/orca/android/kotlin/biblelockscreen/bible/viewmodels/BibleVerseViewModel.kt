package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.duke.orca.android.kotlin.biblelockscreen.base.viewmodels.BaseViewModel
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Verse
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Font
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BookRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.VerseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BibleVerseViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val verseRepository: VerseRepository,
    application: Application
) : BaseViewModel(application) {
    private val verse = MutableLiveData<Verse>()

    val bibleBook by lazy { bookRepository.get() }

    val pair = MediatorLiveData<Pair<Verse, Font>?>().apply {
        addSource(font) {
            value = combine(font, verse)
        }

        addSource(verse) {
            value = combine(font, verse)
        }
    }

    private fun combine(
        source1: LiveData<Font>,
        source2: LiveData<Verse>
    ): Pair<Verse, Font>? {
        val attributeSet = source1.value ?: return null
        val bibleVerse = source2.value ?: return null

        return bibleVerse to attributeSet
    }

    fun get(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            verseRepository.get(id).flowOn(Dispatchers.IO).collect {
                verse.postValue(it)
            }
        }
    }

    fun updateFavorites(id: Int, favorites: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            verseRepository.updateFavorite(id, favorites)
        }
    }
}