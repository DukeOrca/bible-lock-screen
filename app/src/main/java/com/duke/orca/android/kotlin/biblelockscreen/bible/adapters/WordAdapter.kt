package com.duke.orca.android.kotlin.biblelockscreen.bible.adapters

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duke.orca.android.kotlin.biblelockscreen.application.*
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Font
import com.duke.orca.android.kotlin.biblelockscreen.databinding.OptionsMenuBarBinding
import com.duke.orca.android.kotlin.biblelockscreen.databinding.WordItemBinding

class WordAdapter(context: Context) : ListAdapter<WordAdapter.AdapterItem, WordAdapter.ViewHolder>(DiffCallback()) {
    private val layoutInflater = LayoutInflater.from(context)

    private var currentFocus: Int = -1
    private var font: Font? = null
    private var onOptionsItemSelectedListener: OnOptionsItemSelectedListener? = null

    interface OnOptionsItemSelectedListener {
        fun onOptionsItemSelected(item: AdapterItem.Word, optionsItem: OptionsItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(WordItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    private fun setCurrentFocus(position: Int) {
        if (currentFocus.`is`(position)) return

        notifyItemChanged(currentFocus)

        with(position) {
            currentFocus = this
            notifyItemChanged(this)
        }
    }

    fun setFont(font: Font) {
        this.font = font
        notifyItemRangeChanged(0, itemCount)
    }

    fun setOnOptionsItemSelectedListener(onOptionsItemSelectedListener: OnOptionsItemSelectedListener) {
        this.onOptionsItemSelectedListener = onOptionsItemSelectedListener
    }

    inner class ViewHolder(private val viewBinding: WordItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(position: Int) {
            when(val item = getItem(position)) {
                is AdapterItem.Word -> {
                    val verse = item.verse.toString()

                    with(viewBinding) {
                        font?.let {
                            textViewWord.setTextSize(TypedValue.COMPLEX_UNIT_DIP, it.size)
                            textViewWord.gravity = it.textAlignment

                            textViewSubWord.setTextSize(TypedValue.COMPLEX_UNIT_DIP, it.size)
                            textViewSubWord.gravity = it.textAlignment
                        }

                        textViewVerse.text = verse
                        textViewWord.text = item.word

                        item.subWord?.let {
                            textViewSubVerse.text = verse
                            textViewSubWord.text = it
                        } ?: run {
                            textViewSubVerse.hide()
                            textViewSubWord.hide()
                        }

                        if (currentFocus.`is`(position)) {
                            optionsMenuBar.bind(item)
                            optionsMenuBar.root.expand(Duration.EXPAND)
                        } else {
                            with(optionsMenuBar.root) {
                                if (isVisible) {
                                    collapse(Duration.COLLAPSE)
                                }
                            }
                        }

                        constraintLayout.setOnLongClickListener {
                            setCurrentFocus(position)
                            true
                        }
                    }
                }
            }
        }

        private fun OptionsMenuBarBinding.bind(item: AdapterItem.Word) {
            imageViewHighlight.setOnClickListener {
                onOptionsItemSelectedListener?.onOptionsItemSelected(item, OptionsItem.Highlight)
            }

            imageViewHighlightColor.setOnClickListener {
                onOptionsItemSelectedListener?.onOptionsItemSelected(item, OptionsItem.HighlightColor)
            }

            imageViewBookmark.setOnClickListener {
                onOptionsItemSelectedListener?.onOptionsItemSelected(item, OptionsItem.Bookmark)
            }

            imageViewFavorite.setOnClickListener {
                onOptionsItemSelectedListener?.onOptionsItemSelected(item, OptionsItem.Favorite)
            }

            imageViewMore.setOnClickListener {
                onOptionsItemSelectedListener?.onOptionsItemSelected(item, OptionsItem.More)
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

        data class Word(
            override val id: Int,
            val book: Book,
            val subBook: Book? = null,
            val verse: Int,
            val word: String,
            val subWord: String? = null,
            val bookmark: Boolean,
            @ColorInt val color: Int = -1,
            val favorite: Boolean,
        ): AdapterItem() {
            data class Book(
                val id: Int,
                val name: String
            )
        }
    }

    sealed class OptionsItem {
        object Highlight : OptionsItem()
        object HighlightColor: OptionsItem()
        object Bookmark : OptionsItem()
        object Favorite : OptionsItem()
        object More : OptionsItem()
    }
}