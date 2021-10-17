package com.duke.orca.android.kotlin.biblelockscreen.application

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun hideSoftKeyboard(editText: EditText) {
    val context = editText.context

    with(context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
        hideSoftInputFromWindow(editText.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}

fun showSoftKeyboard(editText: EditText) {
    val context = editText.context

    with(context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
        showSoftInput(editText, 0)
    }
}