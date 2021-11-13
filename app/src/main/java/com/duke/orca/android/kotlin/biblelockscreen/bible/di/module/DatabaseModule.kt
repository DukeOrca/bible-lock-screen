package com.duke.orca.android.kotlin.biblelockscreen.bible.di.module

import android.content.Context
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.Database
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.SubDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object DatabaseModule {
    @Provides
    fun provideDatabase(@ApplicationContext applicationContext: Context): Database {
        return Database.getInstance(applicationContext)
    }

    @Provides
    fun provideSubDatabase(@ApplicationContext applicationContext: Context): SubDatabase {
        return SubDatabase.getInstance(applicationContext)
    }
}