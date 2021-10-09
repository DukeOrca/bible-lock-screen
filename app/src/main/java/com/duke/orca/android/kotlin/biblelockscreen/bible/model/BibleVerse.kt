package com.duke.orca.android.kotlin.biblelockscreen.bible.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "bible_verse")
@Parcelize
data class BibleVerse(
    @PrimaryKey
    val id: Int,
    val book: Int,
    val chapter: Int,
    val verse: Int,
    val word: String,
    var favorites: Boolean
): Parcelable