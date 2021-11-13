package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import android.app.Application
import com.duke.orca.android.kotlin.biblelockscreen.base.viewmodels.BaseViewModel
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleBookRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleVerseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BibleVersePagerViewModel @Inject constructor(
    private val bibleBookRepository: BibleBookRepository,
    private val bibleVerseRepository: BibleVerseRepository,
    application: Application
) : BaseViewModel(application) {
    val bibleBook by lazy { bibleBookRepository.get() }
    val behaviorSubject: BehaviorSubject<Any> by lazy { BehaviorSubject.create() }

    fun get(id: Int) {
        bibleVerseRepository.single(id)
            .subscribe ({
                behaviorSubject.onNext(it)
            }) {
                Timber.e(it)
            }
    }

    suspend fun getVerseCount(book: Int, chapter: Int): Int {
        return bibleVerseRepository.getVerseCount(book, chapter)
    }

    suspend fun get(book: Int, chapter: Int, verse: Int): BibleVerse? {
        return bibleVerseRepository.get(book, chapter, verse).first()
    }
}