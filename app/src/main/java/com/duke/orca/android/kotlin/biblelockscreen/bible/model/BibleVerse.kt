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
    val bookmark: Boolean,
    val chapter: Int,
    val favorites: Boolean,
    val verse: Int,
    val word: String
): Parcelable