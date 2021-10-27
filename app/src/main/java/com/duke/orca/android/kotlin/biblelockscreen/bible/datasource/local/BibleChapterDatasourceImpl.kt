package com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local

import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BookChapter
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.BibleDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BibleChapterDatasourceImpl @Inject constructor(private val database: BibleDatabase) : BibleChapterDatasource {
    override fun get(id: Int): Flow<BibleChapter> {
        return database.bibleChapterDao().get(id)
    }

    override fun get(book: Int, chapter: Int): Flow<BibleChapter> {
        return database.bibleChapterDao().get(book, chapter)
    }

    override fun getAll(): Flow<List<BookChapter>> {
        return database.bibleChapterDao().getAll()
    }

    override fun getBookmarks(): Flow<List<BibleChapter>> {
        return database.bibleChapterDao().getBookmarks()
    }

    override suspend fun updateBookmark(id: Int, bookmark: Boolean) {
        database.bibleChapterDao().updateBookmark(id, bookmark)
    }

    override suspend fun updatePosition(id: Int, position: Int) {
        database.bibleChapterDao().updatePosition(id, position)
    }
}