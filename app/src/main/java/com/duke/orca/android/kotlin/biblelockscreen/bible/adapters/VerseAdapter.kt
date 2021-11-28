package com.duke.orca.android.kotlin.biblelockscreen.bible.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.BLANK
import com.duke.orca.android.kotlin.biblelockscreen.application.setTextWithSearchWord
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Book
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Verse
import com.duke.orca.android.kotlin.biblelockscreen.databinding.NativeAdBinding
import com.duke.orca.android.kotlin.biblelockscreen.databinding.VerseItemBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.like.LikeButton
import com.like.OnLikeListener

class VerseAdapter(
    private val book: Book,
    private val onItemClick: ((item: Verse) -> Unit)? = null
) : ListAdapter<VerseAdapter.AdapterItem, VerseAdapter.ViewHolder>(DiffCallback()) {
    private var inflater: LayoutInflater? = null
    private var onIconClickListener: OnIconClickListener? = null
    private var recyclerView: RecyclerView? = null

    private var searchWord = BLANK
    @ColorInt
    private var color = 0

    interface OnIconClickListener {
        fun onFavoriteClick(verse: Verse, favorites: Boolean)
        fun onMoreVertClick(verse: Verse)
    }

    fun setOnIconClickListener(onIconClickListener: OnIconClickListener) {
        this.onIconClickListener = onIconClickListener
    }

    fun submitList(
        list: List<AdapterItem>,
        searchWord: String,
        @ColorInt color: Int,
        commitCallback: (() -> Unit)? = null
    ) {
        this.searchWord = searchWord
        this.color = color

        recyclerView?.scheduleLayoutAnimation()

        submitList(list) {
            commitCallback?.invoke()
        }
    }

    inner class ViewHolder(private val viewBinding: ViewBinding): RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: AdapterItem) {
            when(item) {
                is AdapterItem.AdapterBibleVerse -> {
                    if (viewBinding is VerseItemBinding) {
                        val bibleVerse = item.verse
                        val book = bibleVerse.book
                        val chapter = bibleVerse.chapter
                        val verse = bibleVerse.verse
                        val word = bibleVerse.word

                        val favorite = bibleVerse.favorite

                        viewBinding.textViewBook.text = this@VerseAdapter.book.name(book)
                        viewBinding.textViewChapter.text = chapter.toString()
                        viewBinding.textViewVerse.text = verse.toString()

                        if (searchWord.isBlank()) {
                            viewBinding.textViewWord.text = word
                        } else {
                            viewBinding.textViewWord.setTextWithSearchWord(
                                word,
                                searchWord,
                                color
                            )
                        }

                        viewBinding.likeButton.isLiked = favorite
                        viewBinding.likeButton.setOnLikeListener(object : OnLikeListener {
                            override fun liked(likeButton: LikeButton?) {
                                onIconClickListener?.onFavoriteClick(bibleVerse, true)
                            }

                            override fun unLiked(likeButton: LikeButton?) {
                                onIconClickListener?.onFavoriteClick(bibleVerse, false)
                            }
                        })

                        viewBinding.imageViewMoreVert.setOnClickListener {
                            onIconClickListener?.onMoreVertClick(bibleVerse)
                        }

                        onItemClick?.let { onItemClick ->
                            viewBinding.root.setOnClickListener {
                                onItemClick(bibleVerse)
                            }
                        }
                    }
                }
                is AdapterItem.AdapterNativeAd -> {
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) is AdapterItem.AdapterBibleVerse) {
            ViewType.BIBLE_VERSE
        } else {
            ViewType.NATIVE_AD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = this.inflater ?: LayoutInflater.from(parent.context)
        this.inflater = inflater

        val viewBinding = when(viewType) {
            ViewType.BIBLE_VERSE -> VerseItemBinding.inflate(inflater, parent, false)
            ViewType.NATIVE_AD -> NativeAdBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException()
        }

        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private object ViewType {
            const val BIBLE_VERSE = 0
            const val NATIVE_AD = 1
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

        data class AdapterBibleVerse(val verse: Verse): AdapterItem() {
            override val id = verse.id
        }

        data class AdapterNativeAd(
            override val id: Int,
            val nativeAd: NativeAd
        ): AdapterItem()
    }
}