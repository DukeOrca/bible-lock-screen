package com.duke.orca.android.kotlin.biblelockscreen.bible.repositories

import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BookDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Book
import javax.inject.Inject

class SubBookRepositoryImpl @Inject constructor(private val datasource: BookDatasource) : BookRepository {
    override fun get(): Book {
        return datasource.get()
    }
}