package com.duke.orca.android.kotlin.biblelockscreen.bible.models.datamodels

import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore

data class Font(
    val bold: Boolean,
    val size: Float,
    val textAlignment: Int
) {
    fun contentEquals(other: Font): Boolean {
        if (bold != other.bold) return false
        if (size != other.size) return false
        if (textAlignment != other.textAlignment) return false

        return true
    }

    companion object {
        fun getDefaultFont() = Font(
            false,
            DataStore.Font.DEFAULT_SIZE,
            DataStore.Font.TextAlignment.LEFT
        )
    }
}