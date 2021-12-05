package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.duke.orca.android.kotlin.biblelockscreen.base.viewmodels.BaseViewModel
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Verse
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.datamodels.Font
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BookRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.SubBookRepositoryImpl
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.SubVerseRepositoryImpl
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.VerseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerseViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val verseRepository: VerseRepository,
    application: Application
) : BaseViewModel(application) {
    private val verse = MutableLiveData<Verse>()
    private val subVerse = MutableLiveData<Verse>()

    val bible by lazy { bookRepository.get() }
    val subBible by lazy { subBookRepository?.get() }
    val triple = MediatorLiveData<Triple<Verse, Verse?, Font>?>().apply {
        addSource(font) {
            value = combine(font, verse, subVerse)
        }

        addSource(verse) {
            value = combine(font, verse, subVerse)
        }

        addSource(subVerse) {
            value = combine(font, verse, subVerse)
        }
    }

    private val subBookRepository by lazy {
        SubBookRepositoryImpl.from(application)
    }

    private val subVerseRepository by lazy {
        SubVerseRepositoryImpl.from(application)
    }

    private fun combine(
        source1: LiveData<Font>,
        source2: LiveData<Verse>,
        source3: LiveData<Verse>
    ): Triple<Verse, Verse?, Font>? {
        val font = source1.value ?: return null
        val verse = source2.value ?: return null
        val subVerse = source3.value

        return Triple(verse, subVerse, font)
    }

    fun loadVerseById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            verseRepository.get(id).flowOn(Dispatchers.IO).collect {
                verse.postValue(it)
            }
        }
    }

    fun loadSubVerseById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            subVerseRepository?.get(id)?.flowOn(Dispatchers.IO)?.collect {
                subVerse.postValue(it)
            }
        }
    }

    fun updateFavorites(id: Int, favorites: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            verseRepository.updateFavorite(id, favorites)
        }
    }
}