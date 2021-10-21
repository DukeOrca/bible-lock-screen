package com.duke.orca.android.kotlin.biblelockscreen.bible.repository

import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import kotlinx.coroutines.flow.Flow

interface BibleVerseRepository {
    fun get(id: Int): Flow<BibleVerse?>
    fun get(book: Int, chapter: Int): Flow<List<BibleVerse>>
    fun get(book: Int, chapter: Int, verse: Int): Flow<BibleVerse?>
    fun getFavorites(): Flow<List<BibleVerse>>
    fun search(text: String): Flow<List<BibleVerse>>
    suspend fun getVerseCount(book: Int, chapter: Int): Int
    suspend fun updateFavorites(id: Int, favorites: Boolean)
}