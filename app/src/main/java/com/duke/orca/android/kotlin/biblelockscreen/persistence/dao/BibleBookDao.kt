package com.duke.orca.android.kotlin.biblelockscreen.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleBook
import kotlinx.coroutines.flow.Flow

@Dao
interface BibleBookDao {
    @Query("SELECT * FROM bible_book LIMIT 1")
    fun get(): BibleBook

    @Insert(onConflict = REPLACE)
    fun insert(bibleBook: BibleBook)
}