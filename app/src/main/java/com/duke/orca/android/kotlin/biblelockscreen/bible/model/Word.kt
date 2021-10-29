package com.duke.orca.android.kotlin.biblelockscreen.bible.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.duke.orca.android.kotlin.biblelockscreen.bible.Bible
import kotlinx.parcelize.Parcelize

@Parcelize
data class Word (
    @ColumnInfo(name = "king_james_version")
    val kingJamesVersion: String,
    @ColumnInfo(name = "updated_king_james_version")
    val updatedKingJamesVersion: String,
    @ColumnInfo(name = "american_king_james_version")
    val americanKingJamesVersion: String,
    @ColumnInfo(name = "american_standard_version")
    val americanStandardVersion: String,
    @ColumnInfo(name = "ripv")
    val ripv: String,
    @ColumnInfo(name = "tagalog")
    val tagalog: String,
    @ColumnInfo(name = "cebuano")
    val cebuano: String,
    @ColumnInfo(name = "finnish_bible")
    val finnishBible: String,
    @ColumnInfo(name = "korean_revised_version")
    val koreanRevisedVersion: String,
    @ColumnInfo(name = "romanian_bible")
    val romanianBible: String,
    @ColumnInfo(name = "greek_bible")
    val greekBible: String,
    @ColumnInfo(name = "french_louis_segond")
    val frenchLouisSegond: String,
    @ColumnInfo(name = "german_luther")
    val germanLuther: String,
    @ColumnInfo(name = "italian_bible")
    val italianBible: String,
    @ColumnInfo(name = "spanish_reina")
    val spanishReina: String,
    @ColumnInfo(name = "la_biblia_de_las_americas")
    val spanishLaBibliaDeLasAmericas: String
) : Parcelable {
    fun get(): String {
        return when(Bible.bible) {
            Bible.AMERICAN_KING_JAMES_VERSION -> americanKingJamesVersion
            Bible.AMERICAN_STANDARD_VERSION -> americanStandardVersion
            Bible.CEBUANO -> cebuano
            Bible.FINNISH_BIBLE -> finnishBible
            Bible.FRENCH_LOUIS_SEGOND -> frenchLouisSegond
            Bible.GERMAN_LUTHER -> germanLuther
            Bible.GREEK_BIBLE -> greekBible
            Bible.ITALIAN_BIBLE -> italianBible
            Bible.KING_JAMES_VERSION -> kingJamesVersion
            Bible.KOREAN_REVISED_VERSION -> koreanRevisedVersion
            Bible.RIPV -> ripv
            Bible.ROMANIAN_BIBLE -> romanianBible
            Bible.SPANISH_LA_BIBLIA_DE_LAS_AMERICAS -> spanishLaBibliaDeLasAmericas
            Bible.SPANISH_REINA -> spanishReina
            Bible.TAGALOG -> tagalog
            Bible.UPDATED_KING_JAMES_VERSION -> updatedKingJamesVersion
            else -> kingJamesVersion
        }
    }
}