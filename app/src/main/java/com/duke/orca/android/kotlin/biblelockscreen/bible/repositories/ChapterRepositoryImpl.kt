package com.duke.orca.android.kotlin.biblelockscreen.bible.repositories

import android.content.Context
import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.ChapterDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.ChapterDatasourceImpl
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BookChapter
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.Database
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChapterRepositoryImpl @Inject constructor(private val datasource: ChapterDatasource) : ChapterRepository {
    override fun get(id: Int): Flow<BibleChapter> {
        return datasource.get(id)
    }

    override fun get(book: Int, chapter: Int): Flow<BibleChapter> {
        return datasource.get(book, chapter)
    }

    override fun getAll(): Flow<List<BookChapter>> {
        return datasource.getAll()
    }

    override fun getBookmarks(): Flow<List<BibleChapter>> {
        return datasource.getBookmarks()
    }

    override suspend fun updateBookmark(id: Int, bookmark: Boolean) {
        datasource.updateBookmark(id, bookmark)
    }

    override suspend fun updatePosition(id: Int, position: Int) {
        datasource.updatePosition(id, position)
    }

    companion object {
        fun from(context: Context): ChapterRepositoryImpl {
            val chapterDatasource = ChapterDatasourceImpl(Database.getInstance(context))
            return ChapterRepositoryImpl(chapterDatasource)
        }
    }
}