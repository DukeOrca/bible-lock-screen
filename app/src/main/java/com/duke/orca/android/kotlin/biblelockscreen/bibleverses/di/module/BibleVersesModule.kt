package com.duke.orca.android.kotlin.biblelockscreen.bibleverses.di.module

import android.content.Context
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.datasource.local.BibleVersesDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.datasource.local.BibleVersesDatasourceImpl
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.repository.BibleVersesRepository
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.repository.BibleVersesRepositoryImpl
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.BibleVersesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BibleVersesModule {
    @Singleton
    @Provides
    fun provideBibleVersesDatabase(@ApplicationContext applicationContext: Context): BibleVersesDatabase {
        return BibleVersesDatabase.getInstance(applicationContext)
    }

    @Singleton
    @Provides
    fun provideBibleVersesDatasource(database: BibleVersesDatabase): BibleVersesDatasource {
        return BibleVersesDatasourceImpl(database)
    }

    @Singleton
    @Provides
    fun provideBibleVersesRepository(datasource: BibleVersesDatasource): BibleVersesRepository {
        return BibleVersesRepositoryImpl(datasource)
    }
}