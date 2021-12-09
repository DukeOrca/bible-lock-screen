package com.duke.orca.android.kotlin.biblelockscreen.bible.models.datamodels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Content(
    val book: Int,
    val chapter: Int,
    val verse: Int,
    val word: String
) : Parcelable