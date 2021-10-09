package com.duke.orca.android.kotlin.biblelockscreen.bible.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Parcelize
class BookChapter (
    @ColumnInfo(name = "book")
    val book: Int,
    @ColumnInfo(name = "chapter")
    val chapter: Int
) : Parcelable