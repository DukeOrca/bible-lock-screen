package com.duke.orca.android.kotlin.biblelockscreen.application

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun hideSoftKeyboard(context: Context) {
    with(context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
        toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}

fun showSoftKeyboard(context: Context) {
    with(context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
        toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}

fun showSoftKeyboard(editText: EditText) {
    val context = editText.context

    with(context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
        showSoftInput(editText, 0)
    }
}