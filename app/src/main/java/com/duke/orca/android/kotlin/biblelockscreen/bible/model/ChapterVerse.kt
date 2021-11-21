package com.duke.orca.android.kotlin.biblelockscreen.bible.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChapterVerse(
    val chapter: Int,
    val verse: Int
) : Parcelable