package com.duke.orca.android.kotlin.biblelockscreen.bible.repositories

import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BibleBookDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleBook
import javax.inject.Inject

class BibleBookRepositoryImpl @Inject constructor(private val datasource: BibleBookDatasource) : BibleBookRepository {
    override fun get(): BibleBook {
        return datasource.get()
    }
}