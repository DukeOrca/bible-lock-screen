package com.duke.orca.android.kotlin.biblelockscreen.persistence.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
                CREATE TABLE verse (
                    id INTEGER PRIMARY KEY NOT NULL,
                    book INTEGER NOT NULL,
                    bookmark INTEGER NOT NULL DEFAULT 0,
                    chapter INTEGER NOT NULL,
                    favorite INTEGER NOT NULL DEFAULT 0,
                    highlight_color INTEGER NOT NULL DEFAULT 0,
                    verse INTEGER NOT NULL,
                    word TEXT NOT NULL
                )
                """.trimIndent())
        database.execSQL("""
                INSERT INTO verse (id, book, bookmark, chapter, favorite, verse, word)
                SELECT id, book, bookmark, chapter, favorites, verse, word FROM bible_verse
                """.trimIndent())
        database.execSQL("DROP TABLE bible_verse")
    }
}