package com.duke.orca.android.kotlin.biblelockscreen.bible.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.BLANK
import kotlinx.parcelize.Parcelize

@Entity(tableName = "bible_book")
@Parcelize
data class Book (
    @PrimaryKey
    val names: Array<String>,
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Book

        if (!names.contentEquals(other.names)) return false

        return true
    }

    override fun hashCode(): Int {
        return names.contentHashCode()
    }

    fun name(index: Int): String {
        return if (index.dec() in 0 until names.count()) {
            names[index.dec()]
        } else {
            BLANK
        }
    }
}