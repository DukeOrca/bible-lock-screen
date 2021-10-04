package com.duke.orca.android.kotlin.biblelockscreen.persistence.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.duke.orca.android.kotlin.biblelockscreen.application.Application
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.persistence.dao.BibleVerseDao

@Database(entities = [BibleVerse::class], version = 1)
abstract class BibleVersesDatabase: RoomDatabase() {
    abstract fun bibleVerseDao(): BibleVerseDao

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.persistence.database"
        private const val CLASS_NAME = "BibleVersesDatabase"
        private const val NAME = "$PACKAGE_NAME.$CLASS_NAME"
        private const val VERSION = "1.0.0"

        private const val DATABASE_FILE_PATH = "BibleVerses.db"

        @Volatile
        private var instance: BibleVersesDatabase? = null

        fun getInstance(context: Context): BibleVersesDatabase {
            synchronized(this) {
                var instance = instance

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BibleVersesDatabase::class.java,
                        "$NAME:$VERSION"
                    )
                        .createFromAsset(DATABASE_FILE_PATH)
                        .fallbackToDestructiveMigration()
                        .build()
                    this.instance = instance
                }

                return instance
            }
        }
    }
}