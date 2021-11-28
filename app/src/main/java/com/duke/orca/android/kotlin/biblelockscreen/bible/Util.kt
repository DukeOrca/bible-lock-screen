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
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Book
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Verse

@MainThread
fun copyToClipboard(context: Context, book: Book, verse: Verse) {
    val clipboard: ClipboardManager? = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
    val stringBuilder = StringBuilder()

    stringBuilder.append(verse.word)
    stringBuilder.append("$NEWLINE$NEWLINE")
    stringBuilder.append("${book.name(verse.book)}  ${verse.chapter} : ${verse.verse}")

    with(ClipData.newPlainText("label", stringBuilder.toString())) {
        clipboard?.setPrimaryClip(this)
        Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
    }
}

fun share(context: Context, book: Book, verse: Verse) {
    val intent = Intent(Intent.ACTION_SEND)
    val stringBuilder = StringBuilder()

    stringBuilder.append(verse.word)
    stringBuilder.append("$NEWLINE$NEWLINE")
    stringBuilder.append("${book.name(verse.book)}  ${verse.chapter} : ${verse.verse}")

    intent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString())
    intent.type = "text/plain"

    Intent.createChooser(intent, context.getString(R.string.share)).apply {
        addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        context.startActivity(this)
    }
}