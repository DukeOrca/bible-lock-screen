package com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local

import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Book
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.SubDatabase
import javax.inject.Inject

class SubBookDatasourceImpl @Inject constructor(private val database: SubDatabase) : BookDatasource {
    override fun get(): Book {
        return database.bibleBookDao().get()
    }
}