package com.duke.orca.android.kotlin.biblelockscreen.bibleverses.repository

import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.datasource.local.BibleVersesDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.model.BibleVerse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BibleVersesRepositoryImpl @Inject constructor(private val datasource: BibleVersesDatasource) : BibleVersesRepository {
    override fun get(id: Int): Flow<BibleVerse> {
        return datasource.get(id)
    }

    override fun get(book: Int, chapter: Int, verse: Int): Flow<BibleVerse> {
        return datasource.get(book, chapter, verse)
    }

    override fun getFavorites(): Flow<List<BibleVerse>> {
        return datasource.getFavorites()
    }

    override fun search(text: String): Flow<List<BibleVerse>> {
        return datasource.search(text)
    }

    override suspend fun getVerseCount(book: Int, chapter: Int): Int {
        return datasource.getVerseCount(book, chapter)
    }

    override suspend fun updateFavorites(id: Int, favorites: Boolean) {
        datasource.updateFavorites(id, favorites)
    }
}