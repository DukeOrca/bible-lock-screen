package com.duke.orca.android.kotlin.biblelockscreen.application

import android.content.Context
import android.content.pm.PackageManager
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.BLANK

fun getVersionName(context: Context): String {
    return try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: BLANK
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        return BLANK
    }
}