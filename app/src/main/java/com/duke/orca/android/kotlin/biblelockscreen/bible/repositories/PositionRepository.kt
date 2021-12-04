package com.duke.orca.android.kotlin.biblelockscreen.bible.repositories

import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Position

interface PositionRepository {
    suspend fun insert(position: Position)
    suspend fun get(book: Int, chapter: Int): Int?
}