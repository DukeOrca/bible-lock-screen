package com.duke.orca.android.kotlin.biblelockscreen.bible.di.module

import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BookDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BookDatasourceImpl
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BookRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BookRepositoryImpl
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object BookModule {
    @Provides
    fun provideBookDatasource(database: Database): BookDatasource {
        return BookDatasourceImpl(database)
    }

    @Provides
    fun provideBookRepository(datasource: BookDatasource): BookRepository {
        return BookRepositoryImpl(datasource)
    }
}