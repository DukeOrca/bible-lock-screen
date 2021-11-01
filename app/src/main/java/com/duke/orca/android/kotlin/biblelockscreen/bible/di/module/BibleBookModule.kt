package com.duke.orca.android.kotlin.biblelockscreen.bible.di.module

import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BibleBookDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BibleBookDatasourceImpl
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleBookRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleBookRepositoryImpl
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.BibleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object BibleBookModule {
    @Provides
    fun provideBibleBookDatasource(database: BibleDatabase): BibleBookDatasource {
        return BibleBookDatasourceImpl(database)
    }

    @Provides
    fun provideBibleBookRepository(datasource: BibleBookDatasource): BibleBookRepository {
        return BibleBookRepositoryImpl(datasource)
    }
}