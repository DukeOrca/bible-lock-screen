package com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local

import androidx.annotation.ColorInt
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Verse
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow

interface VerseDatasource {
    fun get(id: Int): Flow<Verse>
    fun get(book: Int, chapter: Int): Flow<List<Verse>>
    fun get(book: Int, chapter: Int, verse: Int): Flow<Verse?>
    fun getFavorites(): Flow<List<Verse>>
    fun search(text: String): Flow<List<Verse>>
    fun single(id: Int): Single<Verse>
    fun loadBookmarks(): Flowable<List<Verse>>
    fun loadHighlights(): Flowable<List<Verse>>
    suspend fun getVerseCount(book: Int, chapter: Int): Int
    suspend fun updateBookmark(id: Int, bookmark: Boolean)
    suspend fun updateFavorite(id: Int, favorite: Boolean)
    suspend fun updateHighlightColor(id: Int, @ColorInt highlightColor: Int)
}