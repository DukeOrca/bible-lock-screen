package com.duke.orca.android.kotlin.biblelockscreen.bible

import android.content.Context
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.BLANK
import com.duke.orca.android.kotlin.biblelockscreen.datastore.PreferencesKeys
import com.duke.orca.android.kotlin.biblelockscreen.datastore.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import java.util.*

object Bible {
    const val AMERICAN_KING_JAMES_VERSION = "americanKingJamesVersion"
    const val AMERICAN_STANDARD_VERSION = "americanStandardVersion"
    const val CEBUANO = "cebuano"
    const val FINNISH_BIBLE = "finnishBible"
    const val FRENCH_LOUIS_SEGOND = "frenchLouisSegond"
    const val GERMAN_LUTHER = "germanLuther"
    const val GREEK_BIBLE = "greekBible"
    const val ITALIAN_BIBLE = "italianBible"
    const val KING_JAMES_VERSION = "kingJamesVersion"
    const val KOREAN_REVISED_VERSION = "koreanRevisedVersion"
    const val RIPV = "ripv"
    const val ROMANIAN_BIBLE = "romanianBible"
    const val SPANISH_LA_BIBLIA_DE_LAS_AMERICAS = "spanishLaBibliaDeLasAmericas"
    const val SPANISH_REINA = "spanishReina"
    const val TAGALOG = "tagalog"
    const val UPDATED_KING_JAMES_VERSION = "updatedKingJamesVersion"

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
            Locale.KOREAN.language -> KOREAN_REVISED_VERSION
            else -> KING_JAMES_VERSION
        }
    }
}