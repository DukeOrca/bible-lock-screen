package com.duke.orca.android.kotlin.biblelockscreen.bible.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "bible_chapter")
@Parcelize
data class BibleChapter(
    @PrimaryKey
    val id: Int,
    val book: Int,
    val bookmark: Boolean,
    val chapter: Int,
    val position: Int
): Parcelable