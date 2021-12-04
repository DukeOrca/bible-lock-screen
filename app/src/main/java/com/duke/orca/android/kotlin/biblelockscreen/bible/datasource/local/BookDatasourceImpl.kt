package com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local

import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Bible
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.Database
import javax.inject.Inject

class BookDatasourceImpl @Inject constructor(private val database: Database) : BookDatasource {
    override fun get(): Bible {
        return database.bibleBookDao().get()
    }
}