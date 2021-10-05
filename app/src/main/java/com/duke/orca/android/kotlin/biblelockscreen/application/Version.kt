package com.duke.orca.android.kotlin.biblelockscreen.application

import android.content.Context
import android.content.pm.PackageManager

fun getVersionName(context: Context): String {
    return try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        return BLANK
    }
}