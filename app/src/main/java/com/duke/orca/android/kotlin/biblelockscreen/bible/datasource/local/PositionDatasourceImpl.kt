package com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local

import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Position
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.Database
import javax.inject.Inject

class PositionDatasourceImpl @Inject constructor(private val database: Database) : PositionDatasource {
    override suspend fun insert(position: Position) {
        database.positionDao().insert(position)
    }

    override suspend fun get(book: Int, chapter: Int): Int? {
        return database.positionDao().get(book, chapter)
    }
}