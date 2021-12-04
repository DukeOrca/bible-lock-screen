package com.duke.orca.android.kotlin.biblelockscreen.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Position

@Dao
interface PositionDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(position: Position)

    @Query("SELECT value FROM position WHERE book = :book AND chapter = :chapter LIMIT 1")
    suspend fun get(book: Int, chapter: Int): Int?
}