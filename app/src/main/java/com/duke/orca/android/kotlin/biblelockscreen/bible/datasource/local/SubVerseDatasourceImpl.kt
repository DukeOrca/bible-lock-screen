package com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local

import androidx.annotation.ColorInt
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Verse
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.SubDatabase
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubVerseDatasourceImpl @Inject constructor(private val database: SubDatabase) : VerseDatasource {
    override fun get(id: Int): Flow<Verse> {
        return database.verseDao().get(id)
    }

    override fun get(book: Int, chapter: Int): Flow<List<Verse>> {
        return database.verseDao().get(book, chapter)
    }

    override fun get(book: Int, chapter: Int, verse: Int): Flow<Verse?> {
        return database.verseDao().get(book, chapter, verse)
    }

    override fun getFavorites(): Flow<List<Verse>> {
        return database.verseDao().getFavorites()
    }

    override fun search(text: String): Flow<List<Verse>> {
        return database.verseDao().search(text)
    }

    override fun single(id: Int): Single<Verse> {
        return database.verseDao().single(id)
    }

    override fun loadHighlights(): Flowable<List<Verse>> {
        return database.verseDao().loadHighlights()
    }

    override suspend fun getVerseCount(book: Int, chapter: Int): Int {
        return database.verseDao().getVerseCount(book, chapter)
    }

    override suspend fun updateBookmark(id: Int, bookmark: Boolean) {
        database.verseDao().updateBookmark(id, bookmark)
    }

    override suspend fun updateFavorite(id: Int, favorite: Boolean) {
        database.verseDao().updateFavorite(id, favorite)
    }

    override suspend fun updateHighlightColor(id: Int, @ColorInt highlightColor: Int) {
        database.verseDao().updateHighlightColor(id, highlightColor)
    }
}