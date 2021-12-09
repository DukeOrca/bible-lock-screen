package com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries

import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.datamodels.Content
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
): Parcelable {
    val position: Position
        get() = Position(book, chapter, verse)

    val content: Content
        get() = Content(book, chapter, verse, word)
}