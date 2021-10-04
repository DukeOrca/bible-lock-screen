package com.duke.orca.android.kotlin.biblelockscreen.bibleverses.datasource.local

import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.BibleVersesDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BibleVersesDatasourceImpl @Inject constructor(private val database: BibleVersesDatabase) : BibleVersesDatasource {
    override fun get(id: Int): Flow<BibleVerse> {
        return database.bibleVerseDao().get(id)
    }

    override fun get(book: Int, chapter: Int, verse: Int): Flow<BibleVerse> {
        return database.bibleVerseDao().get(book, chapter, verse)
    }

    override fun getFavorites(): Flow<List<BibleVerse>> {
        return database.bibleVerseDao().getFavorites()
    }

    override fun search(text: String): Flow<List<BibleVerse>> {
        return database.bibleVerseDao().search(text)
    }

    override suspend fun getVerseCount(book: Int, chapter: Int): Int {
        return database.bibleVerseDao().getVerseCount(book, chapter)
    }

    override suspend fun updateFavorites(id: Int, favorites: Boolean) {
        database.bibleVerseDao().updateFavorites(id, favorites)
    }
}