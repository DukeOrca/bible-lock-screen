package com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local

import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Position

interface PositionDatasource {
    suspend fun insert(position: Position)
    suspend fun get(book: Int, chapter: Int): Int?
}