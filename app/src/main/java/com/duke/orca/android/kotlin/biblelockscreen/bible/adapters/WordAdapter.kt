package com.duke.orca.android.kotlin.biblelockscreen.bible.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Parcelable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.*
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.datamodels.Font
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Verse
import com.duke.orca.android.kotlin.biblelockscreen.databinding.OptionsMenuBarBinding
import com.duke.orca.android.kotlin.biblelockscreen.databinding.WordItemBinding
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.parcelize.Parcelize

class WordAdapter(context: Context) : ListAdapter<WordAdapter.AdapterItem, WordAdapter.ViewHolder>(DiffCallback()) {
    private val layoutInflater = LayoutInflater.from(context)

    private var currentFocus: Int = -1
    private var currentFont: Font? = null
    @ColorInt
    private var highlightColor: Int = DataStore.HighlightColor.DEFAULT
    private var recyclerView: RecyclerView? = null
    private var onOptionsItemSelectedListener: OnOptionsItemSelectedListener? = null

    interface OnOptionsItemSelectedListener {
        fun onOptionsItemSelected(item: AdapterItem.Word, optionsItem: OptionsItem)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(WordItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    private fun setCurrentFocus(position: Int) {
        currentFocus = if (currentFocus.`is`(position)) {
            -1
        } else {
            notifyItemChanged(currentFocus)
            position
        }

        notifyItemChanged(position)
    }

    fun setFont(font: Font) {
        this.currentFont?.let {
            if(it.contentEquals(font).not()) {
                this.currentFont = font
                notifyItemRangeChanged(0, itemCount)
            }
        } ?: run {
            this.currentFont = font
            notifyItemRangeChanged(0, itemCount)
        }
    }

    fun setHighlightColor(@ColorInt highlightColor: Int) {
        this.highlightColor = highlightColor

        if (currentFocus.not(-1)) {
            notifyItemChanged(currentFocus)
        }
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
                        currentFont?.let {
                            textViewVerse.setTextSize(TypedValue.COMPLEX_UNIT_DIP, it.size)
                            textViewWord.setTextSize(TypedValue.COMPLEX_UNIT_DIP, it.size)
                            textViewWord.gravity = it.textAlignment

                            textViewSubVerse.setTextSize(TypedValue.COMPLEX_UNIT_DIP, it.size)
                            textViewSubWord.setTextSize(TypedValue.COMPLEX_UNIT_DIP, it.size)
                            textViewSubWord.gravity = it.textAlignment
                        }

                        textViewVerse.text = verse
                        textViewWord.text = item.word

                        if (item.highlightColor.isZero()) {
                            textViewWord.backgroundTintList = null
                        } else {
                            textViewWord.backgroundTintList = ColorStateList.valueOf(item.highlightColor)
                        }

                        item.subWord?.let {
                            textViewSubVerse.text = verse
                            textViewSubWord.text = it

                            if (item.highlightColor.isZero()) {
                                textViewSubWord.backgroundTintList = null
                            } else {
                                textViewSubWord.backgroundTintList = ColorStateList.valueOf(item.highlightColor)
                            }
                        } ?: run {
                            textViewSubVerse.hide()
                            textViewSubWord.hide()
                        }

                        if (currentFocus.`is`(position)) {
                            optionsMenuBar.bind(item)

                            with(frameLayout) {
                                if (isVisible.not()) {
                                    expand(Duration.EXPAND)
                                }
                            }
                        } else {
                            with(frameLayout) {
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
            val context = root.context

            textViewVerse.text = item.verse.toString()

            if (item.highlightColor.isNonZero()) {
                imageViewHighlight.setColorFilter(
                    item.highlightColor,
                    PorterDuff.Mode.SRC_ATOP
                )
            } else {
                imageViewHighlight.setColorFilter(
                    ContextCompat.getColor(context, R.color.icon),
                    PorterDuff.Mode.SRC_ATOP
                )
            }

            imageViewHighlight.setOnClickListener {
                onOptionsItemSelectedListener?.onOptionsItemSelected(
                    item,
                    OptionsItem.Highlight(
                        if (item.highlightColor.isZero()) {
                            highlightColor
                        } else {
                            0
                        }
                    )
                )
            }

            imageViewHighlightColor.setColorFilter(
                highlightColor,
                PorterDuff.Mode.SRC_ATOP
            )

            imageViewHighlightColor.setOnClickListener {
                onOptionsItemSelectedListener?.onOptionsItemSelected(item, OptionsItem.HighlightColor)
            }

            likeButtonBookmark.isLiked = item.bookmark

            likeButtonBookmark.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton?) {
                    onOptionsItemSelectedListener?.onOptionsItemSelected(
                        item,
                        OptionsItem.Bookmark(true)
                    )
                }

                override fun unLiked(likeButton: LikeButton?) {
                    onOptionsItemSelectedListener?.onOptionsItemSelected(
                        item,
                        OptionsItem.Bookmark(false)
                    )
                }
            })

            likeButtonFavorite.isLiked = item.favorite

            likeButtonFavorite.setOnLikeListener(object : OnLikeListener {
                override fun liked(likeButton: LikeButton?) {
                    onOptionsItemSelectedListener?.onOptionsItemSelected(
                        item,
                        OptionsItem.Favorite(true)
                    )
                }

                override fun unLiked(likeButton: LikeButton?) {
                    onOptionsItemSelectedListener?.onOptionsItemSelected(
                        item,
                        OptionsItem.Favorite(false)
                    )
                }
            })

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

        @Parcelize
        data class Word(
            override val id: Int,
            val book: Book,
            val subBook: Book? = null,
            val chapter: Int,
            val verse: Int,
            val word: String,
            val subWord: String? = null,
            val bookmark: Boolean,
            val favorite: Boolean,
            @ColorInt val highlightColor: Int,
        ): AdapterItem(), Parcelable {
            @Parcelize
            data class Book(
                val id: Int,
                val name: String
            ) : Parcelable

            fun toVerse() = Verse(
                id = id,
                book = book.id,
                bookmark = bookmark,
                chapter = chapter,
                favorite = favorite,
                highlightColor = highlightColor,
                verse = verse,
                word = word
            )
        }
    }

    sealed class OptionsItem {
        class Highlight(@ColorInt val highlightColor: Int) : OptionsItem()
        object HighlightColor: OptionsItem()
        class Bookmark(val liked: Boolean) : OptionsItem()
        class Favorite(val liked: Boolean) : OptionsItem()
        object More : OptionsItem()
    }
}