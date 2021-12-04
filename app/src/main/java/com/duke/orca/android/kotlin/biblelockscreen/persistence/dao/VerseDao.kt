package com.duke.orca.android.kotlin.biblelockscreen.persistence.dao

import androidx.annotation.ColorInt
import androidx.room.Dao
import androidx.room.Query
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Verse
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface VerseDao {
    @Query("SELECT * FROM verse ORDER BY id ASC")
    fun get(): Flow<List<Verse>>

    @Query("SELECT * FROM verse WHERE id = :id")
    fun get(id: Int): Flow<Verse>

    @Query("SELECT * FROM verse WHERE book = :book AND chapter = :chapter ORDER BY id ASC")
    fun get(book: Int, chapter: Int): Flow<List<Verse>>

    @Query("SELECT * FROM verse WHERE book = :book AND chapter = :chapter AND verse = :verse")
    fun get(book: Int, chapter: Int, verse: Int): Flow<Verse>

    @Query("SELECT * FROM verse WHERE favorite LIKE 1")
    fun getFavorites(): Flow<List<Verse>>

    @Query("SELECT * FROM verse WHERE word LIKE '%' || :text || '%'")
    fun search(text: String): Flow<List<Verse>>

    @Query("SELECT * FROM verse WHERE id = :id")
    fun single(id: Int): Single<Verse>

    @Query("SELECT * FROM verse WHERE highlight_color != 0")
    fun loadHighlights(): Flowable<List<Verse>>

    @Query("SELECT COUNT(id) FROM verse WHERE book = :book AND chapter = :chapter")
    suspend fun getVerseCount(book: Int, chapter: Int): Int

    @Query("UPDATE verse SET bookmark = :bookmark WHERE id = :id")
    suspend fun updateBookmark(id: Int, bookmark: Boolean)

    @Query("UPDATE verse SET favorite = :favorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, favorite: Boolean)

    @Query("UPDATE verse SET highlight_color = :highlightColor WHERE id = :id")
    suspend fun updateHighlightColor(id: Int, @ColorInt highlightColor: Int)

    @Query("UPDATE verse SET word = :word WHERE id = :id")
    suspend fun updateWord(id: Int, word: String)
}