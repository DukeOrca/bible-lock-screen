package com.duke.orca.android.kotlin.biblelockscreen.bible.di.module

import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BibleChapterDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.BibleChapterDatasourceImpl
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleChapterRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleChapterRepositoryImpl
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object BibleChapterModule {
    @Provides
    fun provideBibleChapterDatasource(database: Database): BibleChapterDatasource {
        return BibleChapterDatasourceImpl(database)
    }

    @Provides
    fun provideBibleChapterRepository(datasource: BibleChapterDatasource): BibleChapterRepository {
        return BibleChapterRepositoryImpl(datasource)
    }
}