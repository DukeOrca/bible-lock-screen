package com.duke.orca.android.kotlin.biblelockscreen.bible

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.widget.Toast
import androidx.annotation.MainThread
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.NEWLINE
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleBook
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse

@MainThread
fun copyToClipboard(context: Context, bibleBook: BibleBook, bibleVerse: BibleVerse) {
    val clipboard: ClipboardManager? = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
    val stringBuilder = StringBuilder()

    stringBuilder.append(bibleVerse.word)
    stringBuilder.append("$NEWLINE$NEWLINE")
    stringBuilder.append("${bibleBook.name(bibleVerse.book)}  ${bibleVerse.chapter} : ${bibleVerse.verse}")

    with(ClipData.newPlainText("label", stringBuilder.toString())) {
        clipboard?.setPrimaryClip(this)
        Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
    }
}

fun share(context: Context, bibleBook: BibleBook, bibleVerse: BibleVerse) {
    val intent = Intent(Intent.ACTION_SEND)
    val stringBuilder = StringBuilder()

    stringBuilder.append(bibleVerse.word)
    stringBuilder.append("$NEWLINE$NEWLINE")
    stringBuilder.append("${bibleBook.name(bibleVerse.book)}  ${bibleVerse.chapter} : ${bibleVerse.verse}")

    intent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString())
    intent.type = "text/plain"

    Intent.createChooser(intent, context.getString(R.string.share)).apply {
        addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        context.startActivity(this)
    }
}