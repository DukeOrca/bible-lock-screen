package com.duke.orca.android.kotlin.biblelockscreen.bible.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duke.orca.android.kotlin.biblelockscreen.application.hide
import com.duke.orca.android.kotlin.biblelockscreen.databinding.WordBinding

class WordAdapter(context: Context) : ListAdapter<WordAdapter.AdapterItem, WordAdapter.ViewHolder>(DiffCallback()) {
    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(WordBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val viewBinding: WordBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: AdapterItem) {
            when(item) {
                is AdapterItem.AdapterWord -> {
                    val verse = item.verse.toString()

                    with(viewBinding) {
                        textViewVerse.text = verse
                        textViewWord.text = item.word

                        item.subWord?.let {
                            textViewSubVerse.text = verse
                            textViewSubWord.text = it
                        } ?: run {
                            textViewSubVerse.hide()
                            textViewSubWord.hide()
                        }
                    }
                }
            }

        }
    }

    class DiffCallback: DiffUtil.ItemCallback<AdapterItem>() {
        override fun areItemsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
            return oldItem == newItem
        }
    }

    sealed class AdapterItem {
        abstract val id: Int

        data class AdapterWord(
            override val id: Int,
            val book: Book,
            val subBook: Book? = null,
            val verse: Int,
            val word: String,
            val subWord: String? = null,
            val bookmark: Boolean,
            @ColorInt val color: Int = -1,
            val favorites: Boolean,
        ): AdapterItem() {
            data class Book(
                val id: Int,
                val name: String
            )
        }
    }
}