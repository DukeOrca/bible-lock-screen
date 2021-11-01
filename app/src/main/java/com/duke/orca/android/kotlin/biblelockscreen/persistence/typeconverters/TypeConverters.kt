package com.duke.orca.android.kotlin.biblelockscreen.persistence.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson

class TypeConverters {
    @TypeConverter
    fun arrayToJson(value: Array<String>): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToArray(value: String): Array<String> = Gson().fromJson(value, Array<String>::class.java)
}