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
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.datamodels.Content
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Bible

@MainThread
fun copyToClipboard(context: Context, bible: Bible, content: Content) {
    val clipboard: ClipboardManager? = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
    val stringBuilder = StringBuilder()

    stringBuilder.append(content.word)
    stringBuilder.append("$NEWLINE$NEWLINE")
    stringBuilder.append("${bible.name(content.book)}  ${content.chapter} : ${content.verse}")

    with(ClipData.newPlainText("label", stringBuilder.toString())) {
        clipboard?.setPrimaryClip(this)
        Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
    }
}

fun share(context: Context, bible: Bible, content: Content) {
    val intent = Intent(Intent.ACTION_SEND)
    val stringBuilder = StringBuilder()

    stringBuilder.append(content.word)
    stringBuilder.append("$NEWLINE$NEWLINE")
    stringBuilder.append("${bible.name(content.book)}  ${content.chapter} : ${content.verse}")

    intent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString())
    intent.type = "text/plain"

    Intent.createChooser(intent, context.getString(R.string.share)).apply {
        addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        context.startActivity(this)
    }
}