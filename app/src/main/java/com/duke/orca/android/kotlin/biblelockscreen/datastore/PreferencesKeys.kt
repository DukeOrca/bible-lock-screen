package com.duke.orca.android.kotlin.biblelockscreen.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.duke.orca.android.kotlin.biblelockscreen.application.Application

object PreferencesKeys {
    private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.datastore"
    private const val OBJECT_NAME = "PreferencesKeys"

    val isFirstTime = booleanPreferencesKey("$PACKAGE_NAME.$OBJECT_NAME.isFirstTime")

    object BibleVerse {
        private const val OBJECT_NAME = "BibleVerse"
        val currentItem = intPreferencesKey("$PACKAGE_NAME.$OBJECT_NAME.currentItem")
    }

    object Display {
        private const val OBJECT_NAME = "Display"
        val fontSize = floatPreferencesKey("$PACKAGE_NAME.$OBJECT_NAME.fontSize")
        val isDarkMode = booleanPreferencesKey("$PACKAGE_NAME.$OBJECT_NAME.isDarkMode")
    }

    object LockScreen {
        private const val OBJECT_NAME = "LockScreen"
        val displayAfterUnlocking = booleanPreferencesKey("$PACKAGE_NAME.$OBJECT_NAME.displayAfterUnlocking")
        val showOnLockScreen = booleanPreferencesKey("$PACKAGE_NAME.$OBJECT_NAME.showOnLockScreen")
        val unlockWithBackKey = booleanPreferencesKey("$PACKAGE_NAME.$OBJECT_NAME.unlockWithBackKey")
    }
}