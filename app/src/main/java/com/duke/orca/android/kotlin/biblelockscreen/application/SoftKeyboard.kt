package com.duke.orca.android.kotlin.biblelockscreen.application

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.SearchView

fun hideSoftKeyboard(editText: EditText) {
    val context = editText.context

    with(context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
        hideSoftInputFromWindow(editText.windowToken, 0)
    }
}

fun hideSoftKeyboard(searchView: SearchView) {
    val context = searchView.context

    with(context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
        hideSoftInputFromWindow(searchView.windowToken, 0)
    }
}

fun showSoftKeyboard(editText: EditText) {
    val context = editText.context

    with(context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
        showSoftInput(editText, 0)
    }
}