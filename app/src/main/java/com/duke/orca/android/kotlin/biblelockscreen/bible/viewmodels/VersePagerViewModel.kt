package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import android.app.Application
import com.duke.orca.android.kotlin.biblelockscreen.base.viewmodels.BaseViewModel
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Verse
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BookRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.VerseRepository
import com.duke.orca.android.kotlin.biblelockscreen.datastore.recentlyReadDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class VersePagerViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val verseRepository: VerseRepository,
    application: Application
) : BaseViewModel(application) {
    val bibleBook by lazy { bookRepository.get() }
    val behaviorSubject: BehaviorSubject<Any> by lazy { BehaviorSubject.create() }
    val recentlyRead = application.recentlyReadDataStore.data.flowOn(Dispatchers.IO)

    fun get(id: Int) {
        verseRepository.single(id)
            .subscribe ({
                behaviorSubject.onNext(it)
            }) {
                Timber.e(it)
            }
    }

    suspend fun getVerseCount(book: Int, chapter: Int): Int {
        return verseRepository.getVerseCount(book, chapter)
    }

    suspend fun get(book: Int, chapter: Int, verse: Int): Verse? {
        return verseRepository.get(book, chapter, verse).first()
    }
}