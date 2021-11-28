package com.duke.orca.android.kotlin.biblelockscreen.persistence.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Book
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Verse
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Translation
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.persistence.dao.BibleBookDao
import com.duke.orca.android.kotlin.biblelockscreen.persistence.dao.BibleChapterDao
import com.duke.orca.android.kotlin.biblelockscreen.persistence.dao.VerseDao
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.migration.MIGRATION_1_2
import com.duke.orca.android.kotlin.biblelockscreen.persistence.typeconverters.TypeConverters

@androidx.room.Database(entities = [Book::class, BibleChapter::class, Verse::class], version = 2)
@androidx.room.TypeConverters(TypeConverters::class)
abstract class Database: RoomDatabase() {
    abstract fun bibleBookDao(): BibleBookDao
    abstract fun bibleChapterDao(): BibleChapterDao
    abstract fun verseDao(): VerseDao

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.persistence.database"
        private const val CLASS_NAME = "Database"
        private const val NAME = "$PACKAGE_NAME.$CLASS_NAME"
        private const val VERSION = "1.0.1"

        @Volatile
        private var INSTANCE: Database? = null

        fun getInstance(context: Context): Database {
            synchronized(this) {
                return INSTANCE ?: let {
                    getBuilder(context).build().also {
                        INSTANCE = it
                    }
                }
            }
        }

        fun refresh(context: Context) {
            synchronized(this) {
                clear()
                context.applicationContext.cacheDir.deleteRecursively()
                INSTANCE = getBuilder(context).build()
            }
        }

        private fun clear() {
            synchronized(this) {
                INSTANCE?.let {
                    if (it.isOpen) {
                        it.close()
                    }
                }

                INSTANCE = null
            }
        }

        private fun getBuilder(context: Context): Builder<Database> {
            val fileName = DataStore.Translation.getFileName(context).run {
                if (isBlank()) {
                    return@run {
                        Translation.getFileNameInLanguage(context).also {
                            DataStore.Translation.putFileName(context, it)
                        }
                    }
                }

                return@run this
            }

            return Room.databaseBuilder(
                context.applicationContext,
                Database::class.java,
                "$NAME.$fileName:$VERSION"
            )
                .addMigrations(MIGRATION_1_2)
                .allowMainThreadQueries()
                .createFromAsset("$fileName.db")
                .fallbackToDestructiveMigration()
        }
    }
}