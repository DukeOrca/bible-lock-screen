package com.duke.orca.android.kotlin.biblelockscreen.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface BibleVerseDao {
    @Query("SELECT * FROM bible_verse ORDER BY id ASC")
    fun get(): Flow<List<BibleVerse>>

    @Query("SELECT * FROM bible_verse WHERE id = :id")
    fun get(id: Int): Flow<BibleVerse>

    @Query("SELECT * FROM bible_verse WHERE book = :book AND chapter = :chapter ORDER BY id ASC")
    fun get(book: Int, chapter: Int): Flow<List<BibleVerse>>

    @Query("SELECT * FROM bible_verse WHERE book = :book AND chapter = :chapter AND verse = :verse")
    fun get(book: Int, chapter: Int, verse: Int): Flow<BibleVerse>

    @Query("SELECT * FROM bible_verse WHERE favorites LIKE 1")
    fun getFavorites(): Flow<List<BibleVerse>>

    @Query("SELECT * FROM bible_verse WHERE word LIKE '%' || :text || '%'")
    fun search(text: String): Flow<List<BibleVerse>>

    @Query("SELECT * FROM bible_verse WHERE id = :id")
    fun single(id: Int): Single<BibleVerse>

    @Query("SELECT COUNT(id) FROM bible_verse WHERE book = :book AND chapter = :chapter")
    suspend fun getVerseCount(book: Int, chapter: Int): Int

    @Query("UPDATE bible_verse SET favorites = :favorites WHERE id = :id")
    suspend fun updateFavorites(id: Int, favorites: Boolean)

    @Query("UPDATE bible_verse SET word = :word WHERE id = :id")
    suspend fun updateWord(id: Int, word: String)
}