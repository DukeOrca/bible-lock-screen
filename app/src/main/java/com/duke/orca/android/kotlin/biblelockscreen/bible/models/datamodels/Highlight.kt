package com.duke.orca.android.kotlin.biblelockscreen.bible.models.datamodels

import androidx.annotation.ColorInt
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Verse

data class Highlight (
    @ColorInt val highlightColor: Int,
    val verses: List<Verse>
)