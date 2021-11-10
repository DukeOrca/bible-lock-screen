package com.duke.orca.android.kotlin.biblelockscreen.bible.model

import android.content.Context
import android.os.Build
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore

data class Translation (
    val abbreviation: String,
    val displayName: String,
    val fileName: String,
    val language: String
) {
    companion object {
        object Abbreviation {
            const val AMERICAN_KING_JAMES_VERSION = "AKJV"
            const val AMERICAN_STANDARD_VERSION = "ASV"
            const val KING_JAMES_VERSION = "KJV"
            const val KOREAN_REVISED_VERSION = "KRV"
            const val LOUIS_SEGOND = "LSG"
            const val LUTHER_BIBLE = "LUT"
            const val NEW_KOREAN_REVISED_VERSION = "NKRV"
            const val UPDATED_KING_JAMES_VERSION = "UKJV"
        }

        object DisplayName {
            const val AMERICAN_KING_JAMES_VERSION = "American King James Version (AKJV)"
            const val AMERICAN_STANDARD_VERSION = "American Standard Version (ASV)"
            const val KING_JAMES_VERSION = "King James Version (KJV)"
            const val KOREAN_REVISED_VERSION = "개역한글 (KRV)"
            const val LOUIS_SEGOND = "Louis Segond"
            const val LUTHER_BIBLE = "Lutherbibel"
            const val NEW_KOREAN_REVISED_VERSION = "개역개정"
            const val UPDATED_KING_JAMES_VERSION = "Updated King James Version (UKJV)"
        }

        object FileName {
            const val AMERICAN_KING_JAMES_VERSION = "AmericanKingJamesVersion"
            const val AMERICAN_STANDARD_VERSION = "AmericanStandardVersion"
            const val KING_JAMES_VERSION = "KingJamesVersion"
            const val KOREAN_REVISED_VERSION = "KoreanRevisedVersion"
            const val LOUIS_SEGOND = "LouisSegond"
            const val LUTHER_BIBLE = "LutherBible"
            const val NEW_KOREAN_REVISED_VERSION = "NewKoreanRevisedVersion"
            const val UPDATED_KING_JAMES_VERSION = "UpdatedKingJamesVersion"
        }

        object Language {
            const val ENGLISH = "en"
            const val FILIPINO = "fil"
            const val FINNISH = "fi"
            const val FRENCH = "fr"
            const val GERMAN = "de"
            const val GREEK = "el"
            const val ITALIAN = "it"
            const val KOREAN = "ko"
            const val ROMANIAN = "ro"
            const val SPANISH = "es"

            private object DisplayName {
                const val ENGLISH = "English"
                const val FRENCH = "Français"
                const val GERMAN = "Deutsch"
                const val ITALIAN = "Italiano"
                const val KOREAN = "한국어"
            }

            fun String.toDisplayName(): String {
                return when(this) {
                    ENGLISH -> DisplayName.ENGLISH
                    FRENCH -> DisplayName.FRENCH
                    GERMAN -> DisplayName.GERMAN
                    ITALIAN -> DisplayName.ITALIAN
                    KOREAN -> DisplayName.KOREAN
                    else -> DisplayName.ENGLISH
                }
            }
        }

        fun getDisplayName(context: Context): String {
            return when (DataStore.Translation.getFileName(context)) {
                FileName.AMERICAN_KING_JAMES_VERSION -> DisplayName.AMERICAN_KING_JAMES_VERSION
                FileName.AMERICAN_STANDARD_VERSION -> DisplayName.AMERICAN_STANDARD_VERSION
                FileName.KING_JAMES_VERSION -> DisplayName.KING_JAMES_VERSION
                FileName.KOREAN_REVISED_VERSION -> DisplayName.KOREAN_REVISED_VERSION
                FileName.LOUIS_SEGOND -> DisplayName.LOUIS_SEGOND
                FileName.LUTHER_BIBLE -> DisplayName.LUTHER_BIBLE
                FileName.UPDATED_KING_JAMES_VERSION -> DisplayName.UPDATED_KING_JAMES_VERSION
                FileName.NEW_KOREAN_REVISED_VERSION -> DisplayName.NEW_KOREAN_REVISED_VERSION
                else -> getTransitionDisplayNameInLanguage(context)
            }
        }

        fun getTransitionFileNameInLanguage(context: Context): String {
            val language = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales.get(0).language
            } else {
                context.resources.configuration.locale.language
            }

            return when (language) {
                Language.ENGLISH -> FileName.KING_JAMES_VERSION
                Language.FRENCH -> FileName.LOUIS_SEGOND
                Language.GERMAN -> FileName.LUTHER_BIBLE
                Language.KOREAN -> FileName.KOREAN_REVISED_VERSION
                else -> FileName.KING_JAMES_VERSION
            }
        }

        private fun getTransitionDisplayNameInLanguage(context: Context): String {
            val language = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales.get(0).language
            } else {
                context.resources.configuration.locale.language
            }

            return when (language) {
                Language.ENGLISH -> DisplayName.KING_JAMES_VERSION
                Language.FRENCH -> DisplayName.LOUIS_SEGOND
                Language.GERMAN -> DisplayName.LUTHER_BIBLE
                Language.KOREAN -> DisplayName.KOREAN_REVISED_VERSION
                else -> DisplayName.KING_JAMES_VERSION
            }
        }
    }
}