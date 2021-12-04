package com.duke.orca.android.kotlin.biblelockscreen.bible.repositories

import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Bible

interface BookRepository {
    fun get(): Bible
}