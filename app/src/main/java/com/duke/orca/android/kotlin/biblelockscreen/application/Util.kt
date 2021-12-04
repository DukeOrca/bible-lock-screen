package com.duke.orca.android.kotlin.biblelockscreen.application

import android.content.Context
import android.content.Intent
import com.duke.orca.android.kotlin.biblelockscreen.R

fun shareApplication(context: Context) {
    val intent = Intent(Intent.ACTION_SEND)
    val text = "https://play.google.com/store/apps/details?id=${context.packageName}"

    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, text)

    Intent.createChooser(intent, context.getString(R.string.share_the_app)).also {
        it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        context.startActivity(it)
    }
}

val Any?.isNull: Boolean
    get() = this == null

val Any?.notNull: Boolean
    get() = isNull.not()

fun Int?.isZero() = this == 0
fun Int?.isNonZero() = isZero().not()

fun Int?.`is`(other: Int): Boolean = this == other
fun Int?.not(other: Int): Boolean = `is`(other).not()

fun intRange(from: Int, to: Int) = from..to
fun IntRange.toStringArray() = map { it.toString() }.toTypedArray()

fun String.`is`(other: String): Boolean = this == other
fun String.not(other: String): Boolean = `is`(other).not()

fun Array<Int>.toStringArray() = map { "$it" }.toTypedArray()

inline fun <T: Any> ifLet(vararg elements: T?, closure: (List<T>) -> Unit) {
    if (elements.all { it.notNull }) {
        closure(elements.filterNotNull())
    }
}