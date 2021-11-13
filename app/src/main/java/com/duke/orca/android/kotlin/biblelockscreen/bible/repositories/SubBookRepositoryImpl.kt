package com.duke.orca.android.kotlin.biblelockscreen.bible.repositories

import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.SubBookDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleBook
import javax.inject.Inject

class SubBookRepositoryImpl @Inject constructor(private val datasource: SubBookDatasource) : SubBookRepository {
    override fun get(): BibleBook {
        return datasource.get()
    }
}