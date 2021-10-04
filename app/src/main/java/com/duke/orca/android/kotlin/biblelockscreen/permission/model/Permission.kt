package com.duke.orca.android.kotlin.simpledonelist.permission.model

import androidx.annotation.DrawableRes

data class Permission(
    @DrawableRes
    val icon: Int,
    val isRequired: Boolean,
    val permissions: List<String>,
    val permissionName: String,
)