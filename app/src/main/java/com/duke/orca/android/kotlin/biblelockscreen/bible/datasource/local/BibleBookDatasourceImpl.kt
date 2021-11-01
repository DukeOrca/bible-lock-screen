package com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local

import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleBook
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.BibleDatabase
import javax.inject.Inject

class BibleBookDatasourceImpl @Inject constructor(private val database: BibleDatabase) : BibleBookDatasource {
    override fun get(): BibleBook {
        return database.bibleBookDao().get()
    }
}