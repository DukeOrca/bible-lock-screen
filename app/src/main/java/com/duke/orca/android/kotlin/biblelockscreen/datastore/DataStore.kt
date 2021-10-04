package com.duke.orca.android.kotlin.biblelockscreen.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.duke.orca.android.kotlin.biblelockscreen.application.Application
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "${Application.PACKAGE_NAME}.settings")

object DataStore {
    fun getBoolean(context: Context, key: Preferences.Key<Boolean>, defValue: Boolean): Boolean = runBlocking {
        context.dataStore.data.map {
            it[key] ?: defValue
        }.first()
    }

    fun getInt(context: Context, key: Preferences.Key<Int>, defValue: Int): Int = runBlocking {
        context.dataStore.data.map {
            it[key] ?: defValue
        }.first()
    }

    fun getFloat(context: Context, key: Preferences.Key<Float>, defValue: Float): Float = runBlocking {
        context.dataStore.data.map {
            it[key] ?: defValue
        }.first()
    }

    object BibleVerse {
        fun getCurrentItem(context: Context) = getInt(context, PreferencesKeys.BibleVerse.currentItem, 0)

        fun putCurrentItem(context: Context, value: Int) = runBlocking {
            context.dataStore.edit {
                it[PreferencesKeys.BibleVerse.currentItem] = value
            }
        }
    }

    object LockScreen {
        fun getShowOnLockScreen(context: Context) = getBoolean(context, PreferencesKeys.LockScreen.showOnLockScreen, true)
        fun getUnlockWithBackKey(context: Context) = getBoolean(context, PreferencesKeys.LockScreen.unlockWithBackKey, false)
    }
}