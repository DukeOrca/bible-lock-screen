package com.duke.orca.android.kotlin.biblelockscreen.bible.repositories

import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BookChapter
import kotlinx.coroutines.flow.Flow

interface BibleChapterRepository {
    fun get(id: Int): Flow<BibleChapter>
    fun get(book: Int, chapter: Int): Flow<BibleChapter>
    fun getAll(): Flow<List<BookChapter>>
    fun getBookmarks(): Flow<List<BibleChapter>>
    suspend fun updateBookmark(id: Int, bookmark: Boolean)
    suspend fun updatePosition(id: Int, position: Int)
}