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
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Bible
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Verse
import com.duke.orca.android.kotlin.biblelockscreen.databinding.BookItemBinding
import com.duke.orca.android.kotlin.biblelockscreen.databinding.NativeAdBinding
import com.duke.orca.android.kotlin.biblelockscreen.databinding.VerseItemBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.like.LikeButton
import com.like.OnLikeListener

class VerseAdapter(
    private val bible: Bible,
    private val onItemClick: ((item: Verse) -> Unit)? = null
) : ListAdapter<VerseAdapter.AdapterItem, VerseAdapter.ViewHolder>(DiffCallback()) {
    private var inflater: LayoutInflater? = null
    private var onIconClickListener: OnIconClickListener? = null
    private var recyclerView: RecyclerView? = null

    private var searchWord = BLANK
    @ColorInt
    private var color = 0

    interface OnIconClickListener {
        fun onBookmarkClick(verse: Verse, bookmark: Boolean)
        fun onFavoriteClick(verse: Verse, favorite: Boolean)
        fun onMoreVertClick(verse: Verse)
    }

    fun setOnIconClickListener(onIconClickListener: OnIconClickListener) {
        this.onIconClickListener = onIconClickListener
    }

    fun submitMap(map: Map<AdapterItem.AdapterBook, List<AdapterItem.AdapterVerse>>) {
        val arrayList = arrayListOf<AdapterItem>()

        map.forEach { (book, verses) ->
            arrayList.add(book)
            arrayList.addAll(verses)
        }

        submitList(arrayList)
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
                is AdapterItem.AdapterBook -> {
                    if (viewBinding is BookItemBinding) {
                        viewBinding.root.text = bible.name(item.value)
                    }
                }
                is AdapterItem.AdapterVerse -> {
                    if (viewBinding is VerseItemBinding) {
                        val verse = item.verse
                        val bookmark = verse.bookmark
                        val favorite = verse.favorite

                        viewBinding.textViewBook.text = bible.name(verse.book)
                        viewBinding.textViewChapter.text = "${verse.chapter}"
                        viewBinding.textViewVerse.text = "${verse.verse}"

                        if (searchWord.isBlank()) {
                            viewBinding.textViewWord.text = verse.word
                        } else {
                            viewBinding.textViewWord.setTextWithSearchWord(
                                verse.word,
                                searchWord,
                                color
                            )
                        }

                        viewBinding.likeButtonBookmark.isLiked = bookmark
                        viewBinding.likeButtonBookmark.setOnLikeListener(object : OnLikeListener {
                            override fun liked(likeButton: LikeButton?) {
                                TODO("Not yet implemented")
                            }

                            override fun unLiked(likeButton: LikeButton?) {
                                TODO("Not yet implemented")
                            }
                        })

                        viewBinding.likeButtonFavorite.isLiked = favorite
                        viewBinding.likeButtonFavorite.setOnLikeListener(object : OnLikeListener {
                            override fun liked(likeButton: LikeButton?) {
                                onIconClickListener?.onFavoriteClick(verse, true)
                            }

                            override fun unLiked(likeButton: LikeButton?) {
                                onIconClickListener?.onFavoriteClick(verse, false)
                            }
                        })

                        viewBinding.imageViewMoreVert.setOnClickListener {
                            onIconClickListener?.onMoreVertClick(verse)
                        }

                        onItemClick?.let { onItemClick ->
                            viewBinding.root.setOnClickListener {
                                onItemClick(verse)
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
        return when(getItem(position)) {
            is AdapterItem.AdapterBook -> ViewType.BOOK
            is AdapterItem.AdapterNativeAd -> ViewType.NATIVE_AD
            is AdapterItem.AdapterVerse -> ViewType.VERSE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = this.inflater ?: LayoutInflater.from(parent.context)
        this.inflater = inflater

        val viewBinding = when(viewType) {
            ViewType.BOOK -> BookItemBinding.inflate(inflater, parent, false)
            ViewType.VERSE -> VerseItemBinding.inflate(inflater, parent, false)
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
            const val BOOK = 0
            const val VERSE = 1
            const val NATIVE_AD = 2
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

        data class AdapterBook(val value: Int): AdapterItem() {
            override val id = -1
        }

        data class AdapterVerse(val verse: Verse): AdapterItem() {
            override val id = verse.id
        }

        data class AdapterNativeAd(
            override val id: Int,
            val nativeAd: NativeAd
        ): AdapterItem()
    }
}