package com.duke.orca.android.kotlin.biblelockscreen.bible.repository

import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BibleChapterDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BookChapter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BibleChapterRepositoryImpl @Inject constructor(private val datasource: BibleChapterDatasource) : BibleChapterRepository {
    override fun get(id: Int): Flow<BibleChapter> {
        return datasource.get(id)
    }

    override fun get(book: Int, chapter: Int): Flow<BibleChapter> {
        return datasource.get(book, chapter)
    }

    override fun getAll(): Flow<List<BookChapter>> {
        return datasource.getAll()
    }

    override suspend fun updateBookmark(id: Int, bookmark: Boolean) {
        datasource.updateBookmark(id, bookmark)
    }
}