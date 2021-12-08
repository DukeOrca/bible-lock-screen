package com.duke.orca.android.kotlin.biblelockscreen.application.constants

internal object Duration {
    const val SHORT = 200L
    const val MEDIUM = 400L
    const val LONG = 500L

    object Animation{
        const val COLLAPSE = 300L
        const val EXPAND = 300L
        const val FADE_IN = 500L
        const val FADE_OUT = 400L
        const val ROTATION = 300L
    }

    object Delay {
        const val DISMISS = 200L
        const val RECREATE = 150L
        const val ROTATE = 50L
        const val SLIDE_IN = 400L
        const val START_ACTIVITY = 200L
    }

    object ItemAnimator {
        const val MOVE = 180L
        const val REMOVE = 240L
    }

    const val COLLAPSE = 300L
    const val EXPAND = 300L
    const val FADE_IN = LONG
    const val FADE_OUT = MEDIUM
    const val ROTATION = 300L
}