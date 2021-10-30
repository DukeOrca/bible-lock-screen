package com.duke.orca.android.kotlin.biblelockscreen.bible

import android.content.Context
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.BLANK
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Language
import com.duke.orca.android.kotlin.biblelockscreen.datastore.PreferencesKeys
import com.duke.orca.android.kotlin.biblelockscreen.datastore.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

object Bible {
    const val AMERICAN_KING_JAMES_VERSION = "americanKingJamesVersion"              // en
    const val AMERICAN_STANDARD_VERSION = "americanStandardVersion"                 // en
    const val CEBUANO = "cebuano"                                                   // fil
    const val FINNISH_BIBLE = "finnishBible"                                        // fi
    const val FRENCH_LOUIS_SEGOND = "frenchLouisSegond"                             // fr
    const val GERMAN_LUTHER = "germanLuther"                                        // de
    const val GREEK_BIBLE = "greekBible"                                            // el
    const val ITALIAN_BIBLE = "italianBible"                                        // it
    const val KING_JAMES_VERSION = "kingJamesVersion"                               // en
    const val KOREAN_REVISED_VERSION = "koreanRevisedVersion"                       // ko
    const val RIPV = "ripv"                                                         // fil
    const val ROMANIAN_BIBLE = "romanianBible"                                      // ro
    const val SPANISH_LA_BIBLIA_DE_LAS_AMERICAS = "spanishLaBibliaDeLasAmericas"    // es
    const val SPANISH_REINA = "spanishReina"                                        // es
    const val TAGALOG = "tagalog"                                                   // fil
    const val UPDATED_KING_JAMES_VERSION = "updatedKingJamesVersion"                // en

    var bible = BLANK

    fun toLiveData(context: Context): LiveData<String> {
        return context.dataStore.data.map {
            val language = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                context.resources.configuration.locales.get(0).language
            } else{
                context.resources.configuration.locale.language
            }

            it[PreferencesKeys.Bible.bible] ?: initialBible(language)
        }.asLiveData(Dispatchers.IO)
    }

    fun initialBible(language: String): String {
        return when(language) {
            Language.ENGLISH -> KING_JAMES_VERSION
            Language.FILIPINO -> CEBUANO
            Language.FINNISH -> FINNISH_BIBLE
            Language.FRENCH -> FRENCH_LOUIS_SEGOND
            Language.GERMAN -> GERMAN_LUTHER
            Language.GREEK -> GREEK_BIBLE
            Language.ITALIAN -> ITALIAN_BIBLE
            Language.KOREAN -> KOREAN_REVISED_VERSION
            Language.ROMANIAN -> ROMANIAN_BIBLE
            Language.SPANISH -> SPANISH_REINA
            else -> KING_JAMES_VERSION
        }
    }
}