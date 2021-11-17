package com.duke.orca.android.kotlin.biblelockscreen.bible.di.module

import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BibleBookDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BibleBookDatasourceImpl
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleBookRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleBookRepositoryImpl
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object BookModule {
    @Provides
    fun provideBookDatasource(database: Database): BibleBookDatasource {
        return BibleBookDatasourceImpl(database)
    }

    @Provides
    fun provideBookRepository(datasource: BibleBookDatasource): BibleBookRepository {
        return BibleBookRepositoryImpl(datasource)
    }
}