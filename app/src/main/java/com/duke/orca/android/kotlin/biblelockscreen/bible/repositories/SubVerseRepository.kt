package com.duke.orca.android.kotlin.biblelockscreen.bible.repositories

import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow

interface SubVerseRepository {
    fun get(id: Int): Flow<BibleVerse?>
    fun get(book: Int, chapter: Int): Flow<List<BibleVerse>>
    fun get(book: Int, chapter: Int, verse: Int): Flow<BibleVerse?>
    fun getFavorites(): Flow<List<BibleVerse>>
    fun search(text: String): Flow<List<BibleVerse>>
    fun single(id: Int): Single<BibleVerse>
    suspend fun getVerseCount(book: Int, chapter: Int): Int
    suspend fun updateFavorites(id: Int, favorites: Boolean)
}