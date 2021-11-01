package com.duke.orca.android.kotlin.biblelockscreen.bible.di.module

import android.content.Context
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.BibleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object BibleModule {
    @Provides
    fun provideBibleDatabase(@ApplicationContext applicationContext: Context): BibleDatabase {
        return BibleDatabase.getInstance(applicationContext)
    }
}