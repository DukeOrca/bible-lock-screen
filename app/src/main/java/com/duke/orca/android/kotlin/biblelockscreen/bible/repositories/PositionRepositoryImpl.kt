package com.duke.orca.android.kotlin.biblelockscreen.bible.repositories

import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.PositionDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Position
import javax.inject.Inject

class PositionRepositoryImpl @Inject constructor(private val datasource: PositionDatasource) : PositionRepository {
    override suspend fun insert(position: Position) {
        datasource.insert(position)
    }

    override suspend fun get(book: Int, chapter: Int): Int? {
        return datasource.get(book, chapter)
    }
}