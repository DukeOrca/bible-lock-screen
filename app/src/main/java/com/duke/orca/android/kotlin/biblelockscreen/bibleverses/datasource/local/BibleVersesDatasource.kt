package com.duke.orca.android.kotlin.biblelockscreen.bibleverses.datasource.local

import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.model.BibleVerse
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow

interface BibleVersesDatasource {
    fun get(id: Int): Flow<BibleVerse>
    fun get(book: Int, chapter: Int, verse: Int): Flow<BibleVerse>
    fun getFavorites(): Flow<List<BibleVerse>>
    fun search(text: String): Flow<List<BibleVerse>>
    suspend fun getVerseCount(book: Int, chapter: Int): Int
    suspend fun updateFavorites(id: Int, favorites: Boolean)
}