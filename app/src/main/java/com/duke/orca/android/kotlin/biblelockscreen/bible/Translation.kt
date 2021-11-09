package com.duke.orca.android.kotlin.biblelockscreen.bible

import android.content.Context
import android.os.Build
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.BLANK
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Language
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore

object Translation {
    object DisplayName {
        const val AMERICAN_KING_JAMES_VERSION = "American King James Version"
        const val AMERICAN_STANDARD_VERSION = "American Standard Version"
        const val KING_JAMES_VERSION = "King James Version"
        const val KOREAN_REVISED_VERSION = "개역한글"
        const val LOUIS_SEGOND = "Louis Segond"
        const val LUTHER_BIBLE = "Lutherbibel"
        const val NEW_KOREAN_REVISED_VERSION = "개역개정"
        const val UPDATED_KING_JAMES_VERSION = "Updated King James Version"
    }

    object Name {
        const val AMERICAN_KING_JAMES_VERSION = "AmericanKingJamesVersion"
        const val AMERICAN_STANDARD_VERSION = "AmericanStandardVersion"
        const val KING_JAMES_VERSION = "KingJamesVersion"
        const val KOREAN_REVISED_VERSION = "KoreanRevisedVersion"
        const val LOUIS_SEGOND = "LouisSegond"
        const val LUTHER_BIBLE = "LutherBible"
        const val NEW_KOREAN_REVISED_VERSION = "NewKoreanRevisedVersion"
        const val UPDATED_KING_JAMES_VERSION = "UpdatedKingJamesVersion"
    }

    fun getDisplayName(context: Context): String {
        return when(DataStore.Translation.getTranslation(context)) {
            Name.AMERICAN_KING_JAMES_VERSION -> DisplayName.AMERICAN_KING_JAMES_VERSION
            Name.AMERICAN_STANDARD_VERSION -> DisplayName.AMERICAN_STANDARD_VERSION
            Name.KING_JAMES_VERSION -> DisplayName.KING_JAMES_VERSION
            Name.KOREAN_REVISED_VERSION -> DisplayName.KOREAN_REVISED_VERSION
            Name.LOUIS_SEGOND -> DisplayName.LOUIS_SEGOND
            Name.LUTHER_BIBLE -> DisplayName.LUTHER_BIBLE
            Name.UPDATED_KING_JAMES_VERSION -> DisplayName.UPDATED_KING_JAMES_VERSION
            Name.NEW_KOREAN_REVISED_VERSION -> DisplayName.NEW_KOREAN_REVISED_VERSION
            else -> getInitialTransitionName(context)
        }
    }

    fun getInitialTransitionName(context: Context): String {
        val language = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0).language
        } else {
            context.resources.configuration.locale.language
        }

        return when(language) {
            Language.ENGLISH -> Name.KING_JAMES_VERSION
            Language.FRENCH -> Name.LOUIS_SEGOND
            Language.GERMAN -> Name.LUTHER_BIBLE
            Language.KOREAN -> Name.KOREAN_REVISED_VERSION
            else -> Name.KING_JAMES_VERSION
        }
    }
}