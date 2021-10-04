package com.duke.orca.android.kotlin.biblelockscreen.application

fun Int.toHexColor() = String.format("#%06X", 0xFFFFFF and this)