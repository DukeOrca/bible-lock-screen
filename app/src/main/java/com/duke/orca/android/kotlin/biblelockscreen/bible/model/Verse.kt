package com.duke.orca.android.kotlin.biblelockscreen.bible.model

import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "verse")
@Parcelize
data class Verse(
    @PrimaryKey
    val id: Int,
    val book: Int,
    val bookmark: Boolean,
    val chapter: Int,
    val favorite: Boolean,
    @ColumnInfo(name = "highlight_color")
    @ColorInt val highlightColor: Int,
    val verse: Int,
    val word: String
): Parcelable