package com.duke.orca.android.kotlin.biblelockscreen.networkstatus

sealed class NetworkStatus {
    object Available : NetworkStatus()
    object Unavailable : NetworkStatus()
}