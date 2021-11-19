package com.duke.orca.android.kotlin.biblelockscreen.application.constants

internal object Duration {
    object Delay {
        const val SHORT = 50L
        const val MEDIUM = 150L

        const val DISMISS = MEDIUM
        const val RECREATE = MEDIUM
        const val ROTATE = SHORT
    }

    const val SHORT = 200L
    const val MEDIUM = 400L
    const val LONG = 500L

    const val FADE_IN = LONG
    const val FADE_OUT = MEDIUM
    const val ROTATION = 300L
}