package com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local

import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Bible

interface BookDatasource {
    fun get(): Bible
}