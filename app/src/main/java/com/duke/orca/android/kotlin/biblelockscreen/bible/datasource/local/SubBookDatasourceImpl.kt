package com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local

import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleBook
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.SubDatabase
import javax.inject.Inject

class SubBookDatasourceImpl @Inject constructor(private val database: SubDatabase) : SubBookDatasource {
    override fun get(): BibleBook {
        return database.bibleBookDao().get()
    }
}