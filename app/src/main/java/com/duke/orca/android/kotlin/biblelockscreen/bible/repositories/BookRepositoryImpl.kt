package com.duke.orca.android.kotlin.biblelockscreen.bible.repositories

import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BookDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Bible
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(private val datasource: BookDatasource) : BookRepository {
    override fun get(): Bible {
        return datasource.get()
    }
}