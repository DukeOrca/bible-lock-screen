package com.duke.orca.android.kotlin.biblelockscreen.bible.repositories

import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleBook

interface SubBookRepository {
    fun get(): BibleBook
}