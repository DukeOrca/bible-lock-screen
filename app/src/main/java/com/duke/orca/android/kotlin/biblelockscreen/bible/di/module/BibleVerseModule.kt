package com.duke.orca.android.kotlin.biblelockscreen.bible.di.module

import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BibleVerseDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BibleVerseDatasourceImpl
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleVerseRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleVerseRepositoryImpl
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.BibleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object BibleVerseModule {
    @Provides
    fun provideBibleVerseDatasource(database: BibleDatabase): BibleVerseDatasource {
        return BibleVerseDatasourceImpl(database)
    }

    @Provides
    fun provideBibleVerseRepository(datasource: BibleVerseDatasource): BibleVerseRepository {
        return BibleVerseRepositoryImpl(datasource)
    }
}