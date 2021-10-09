package com.duke.orca.android.kotlin.biblelockscreen.persistence.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BookChapter
import kotlinx.coroutines.flow.Flow

@Dao
interface BibleChapterDao {
    @Query("SELECT * FROM bible_chapter WHERE id = :id")
    fun get(id: Int): Flow<BibleChapter>

    @Query("SELECT * FROM bible_chapter WHERE book = :book AND chapter = :chapter")
    fun get(book: Int, chapter: Int): Flow<BibleChapter>

    @Transaction
    @Query("SELECT book, chapter FROM bible_chapter ORDER BY id ASC")
    fun getAll(): Flow<List<BookChapter>>

    @Query("UPDATE bible_chapter SET bookmark = :bookmark WHERE id = :id")
    suspend fun updateBookmark(id: Int, bookmark: Boolean)
}