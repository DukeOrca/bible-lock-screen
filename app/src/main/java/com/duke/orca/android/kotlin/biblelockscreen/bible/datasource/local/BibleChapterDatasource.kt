package com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local

import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BookChapter
import kotlinx.coroutines.flow.Flow

interface BibleChapterDatasource {
    fun get(id: Int): Flow<BibleChapter>
    fun get(book: Int, chapter: Int): Flow<BibleChapter>
    fun getAll(): Flow<List<BookChapter>>
    fun getBookmarks(): Flow<List<BibleChapter>>
    suspend fun updateBookmark(id: Int, bookmark: Boolean)
}