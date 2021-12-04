package com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local

import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Bible
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.SubDatabase
import javax.inject.Inject

class SubBookDatasourceImpl @Inject constructor(private val database: SubDatabase) : BookDatasource {
    override fun get(): Bible {
        return database.bibleBookDao().get()
    }
}