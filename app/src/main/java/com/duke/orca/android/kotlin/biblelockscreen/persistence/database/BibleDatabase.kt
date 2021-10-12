package com.duke.orca.android.kotlin.biblelockscreen.persistence.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.persistence.dao.BibleChapterDao
import com.duke.orca.android.kotlin.biblelockscreen.persistence.dao.BibleVerseDao

@Database(entities = [BibleChapter::class, BibleVerse::class], version = 1)
abstract class BibleDatabase: RoomDatabase() {
    abstract fun bibleChapterDao(): BibleChapterDao
    abstract fun bibleVerseDao(): BibleVerseDao

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.persistence.database"
        private const val CLASS_NAME = "BibleDatabase"
        private const val NAME = "$PACKAGE_NAME.$CLASS_NAME"
        private const val VERSION = "1.0.0"

        private const val DATABASE_FILE_PATH = "Bible.db"

        @Volatile
        private var instance: BibleDatabase? = null

        fun getInstance(context: Context): BibleDatabase {
            synchronized(this) {
                var instance = instance

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BibleDatabase::class.java,
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