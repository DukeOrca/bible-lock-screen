package com.duke.orca.android.kotlin.biblelockscreen.bible.di.module

import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.ChapterDatasource
import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.ChapterDatasourceImpl
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.ChapterRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.ChapterRepositoryImpl
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ChapterModule {
    @Provides
    fun provideBibleChapterDatasource(database: Database): ChapterDatasource {
        return ChapterDatasourceImpl(database)
    }

    @Provides
    fun provideBibleChapterRepository(datasource: ChapterDatasource): ChapterRepository {
        return ChapterRepositoryImpl(datasource)
    }
}