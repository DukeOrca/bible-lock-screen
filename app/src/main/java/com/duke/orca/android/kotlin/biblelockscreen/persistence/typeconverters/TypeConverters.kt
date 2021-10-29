package com.duke.orca.android.kotlin.biblelockscreen.persistence.typeconverters

import androidx.room.TypeConverter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Word
import com.google.gson.Gson

class TypeConverters {
    @TypeConverter
    fun wordToJson(value: Word): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToWord(value: String): Word = Gson().fromJson(value, Word::class.java)

    @TypeConverter
    fun arrayToJson(value: Array<String>): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToArray(value: String): Array<String> = Gson().fromJson(value, Array<String>::class.java)
}