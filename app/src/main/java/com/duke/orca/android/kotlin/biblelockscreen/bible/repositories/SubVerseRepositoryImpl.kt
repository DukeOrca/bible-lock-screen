package com.duke.orca.android.kotlin.biblelockscreen.bible.repositories

import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.SubVerseDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubVerseRepositoryImpl @Inject constructor(private val datasource: SubVerseDatasource) : SubVerseRepository {
    override fun get(id: Int): Flow<BibleVerse> {
        return datasource.get(id)
    }

    override fun get(book: Int, chapter: Int): Flow<List<BibleVerse>> {
        return datasource.get(book, chapter)
    }

    override fun get(book: Int, chapter: Int, verse: Int): Flow<BibleVerse?> {
        return datasource.get(book, chapter, verse)
    }

    override fun getFavorites(): Flow<List<BibleVerse>> {
        return datasource.getFavorites()
    }

    override fun search(text: String): Flow<List<BibleVerse>> {
        return datasource.search(text)
    }

    override fun single(id: Int): Single<BibleVerse> {
        return datasource.single(id)
    }

    override suspend fun getVerseCount(book: Int, chapter: Int): Int {
        return datasource.getVerseCount(book, chapter)
    }

    override suspend fun updateFavorites(id: Int, favorites: Boolean) {
        datasource.updateFavorites(id, favorites)
    }
}