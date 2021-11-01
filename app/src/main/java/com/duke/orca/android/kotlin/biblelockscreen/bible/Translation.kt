package com.duke.orca.android.kotlin.biblelockscreen.bible

import android.content.Context
import android.os.Build
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Language

object Translation {
    object DisplayName {
        const val KING_JAMES_VERSION = "King James Version"
        const val KOREAN_REVISED_VERSION = "개역한글"
        const val NEW_KOREAN_REVISED_VERSION = "개역개정"
    }

    object Name {
        const val KING_JAMES_VERSION = "KingJamesVersion"
        const val KOREAN_REVISED_VERSION = "KoreanRevisedVersion"
        const val NEW_KOREAN_REVISED_VERSION = "NewKoreanRevisedVersion"
    }

    fun getInitialTransitionName(context: Context): String {
        val language = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0).language
        } else {
            context.resources.configuration.locale.language
        }

        return when(language) {
            Language.ENGLISH -> Name.KING_JAMES_VERSION
            Language.KOREAN -> Name.KOREAN_REVISED_VERSION
            else -> Name.KING_JAMES_VERSION
        }
    }
}