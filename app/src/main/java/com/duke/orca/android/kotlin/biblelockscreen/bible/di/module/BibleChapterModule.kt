package com.duke.orca.android.kotlin.biblelockscreen.bible.di.module

import android.content.Context
import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BibleChapterDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BibleChapterDatasourceImpl
import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BibleVerseDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BibleVerseDatasourceImpl
import com.duke.orca.android.kotlin.biblelockscreen.bible.repository.BibleChapterRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repository.BibleChapterRepositoryImpl
import com.duke.orca.android.kotlin.biblelockscreen.bible.repository.BibleVerseRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repository.BibleVerseRepositoryImpl
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.BibleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BibleChapterModule {
    @Singleton
    @Provides
    fun provideBibleChapterDatasource(database: BibleDatabase): BibleChapterDatasource {
        return BibleChapterDatasourceImpl(database)
    }

    @Singleton
    @Provides
    fun provideBibleChapterRepository(datasource: BibleChapterDatasource): BibleChapterRepository {
        return BibleChapterRepositoryImpl(datasource)
    }
}