package com.duke.orca.android.kotlin.biblelockscreen.permission.model

import androidx.annotation.DrawableRes

data class Permission(
    @DrawableRes
    val icon: Int,
    val isRequired: Boolean,
    val permission: String,
    val permissionName: String,
    val priority: Int,
)