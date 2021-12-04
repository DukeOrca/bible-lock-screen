package com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries

import androidx.room.Entity

@Entity(tableName = "position", primaryKeys = ["book", "chapter"])
data class Position(
    val book: Int,
    val chapter: Int,
    val value: Int
)