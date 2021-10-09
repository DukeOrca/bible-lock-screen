package com.duke.orca.android.kotlin.biblelockscreen.bible.di.module

import android.content.Context
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.BibleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BibleCModule {
    @Singleton
    @Provides
    fun provideBibleDatabase(@ApplicationContext applicationContext: Context): BibleDatabase {
        return BibleDatabase.getInstance(applicationContext)
    }
}