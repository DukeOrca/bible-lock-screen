package com.duke.orca.android.kotlin.biblelockscreen.application.constants

internal object Duration {
    const val SHORT = 200L
    const val MEDIUM = 400L
    const val LONG = 500L

    object Animation{
        const val COLLAPSE = 250L
        const val EXPAND = 250L
        const val FADE_IN = LONG
        const val FADE_OUT = MEDIUM
        const val ROTATION = 250L
    }

    object Delay {
        const val DISMISS = 150L
        const val RECREATE = 150L
        const val ROTATE = 50L
        const val SCROLL = 120L
        const val SLIDE_IN = 360L
        const val START_ACTIVITY = SHORT
    }
}