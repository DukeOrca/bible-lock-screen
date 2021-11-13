package com.duke.orca.android.kotlin.biblelockscreen.bible.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
class BookChapter (
    val id: Int,
    val book: Int,
    val chapter: Int
) : Parcelable