package com.duke.orca.android.kotlin.biblelockscreen.persistence.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleBook
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Translation
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.persistence.dao.BibleBookDao
import com.duke.orca.android.kotlin.biblelockscreen.persistence.dao.BibleChapterDao
import com.duke.orca.android.kotlin.biblelockscreen.persistence.dao.BibleVerseDao

@Database(entities = [BibleBook::class, BibleChapter::class, BibleVerse::class], version = 1)
@TypeConverters(com.duke.orca.android.kotlin.biblelockscreen.persistence.typeconverters.TypeConverters::class)
abstract class BibleDatabase: RoomDatabase() {
    abstract fun bibleBookDao(): BibleBookDao
    abstract fun bibleChapterDao(): BibleChapterDao
    abstract fun bibleVerseDao(): BibleVerseDao

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.persistence.database"
        private const val CLASS_NAME = "BibleDatabase"
        private const val NAME = "$PACKAGE_NAME.$CLASS_NAME"
        private const val VERSION = "1.0.0"

        @Volatile
        private var instance: BibleDatabase? = null

        fun clear() {
            synchronized(this) {
                instance?.let {
                    if (it.isOpen) {
                        it.close()
                    }
                }

                instance = null
            }
        }

        fun getInstance(context: Context): BibleDatabase {
            synchronized(this) {
                val translation = DataStore.Translation.getFileName(context).run {
                    if (isBlank()) {
                        return@run Translation.getTransitionFileNameInLanguage(context)
                    }

                    return@run this
                }

                var instance = instance

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BibleDatabase::class.java,
                        "$NAME.$translation:$VERSION"
                    )
                        .allowMainThreadQueries()
                        .createFromAsset("$translation.db")
                        .fallbackToDestructiveMigration()
                        .build()
                    this.instance = instance
                }

                return instance
            }
        }
    }
}