package com.duke.orca.android.kotlin.biblelockscreen.bible.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.setTint
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Bible
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Verse
import com.duke.orca.android.kotlin.biblelockscreen.databinding.BookItemBinding
import com.duke.orca.android.kotlin.biblelockscreen.databinding.BookmarkItemBinding

class BookmarkAdapter(
    context: Context,
    private val bible: Bible
) : ListAdapter<BookmarkAdapter.AdapterItem, BookmarkAdapter.ViewHolder>(DiffCallback()) {
    private object ViewType {
        const val BOOK = 0
        const val BOOKMARK = 1
    }

    private val format = context.getString(R.string.format_bible_chapter)
    private val inflater = LayoutInflater.from(context)

    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(item: AdapterItem.BookmarkItem)
        fun onIconClick(item: AdapterItem.BookmarkItem)
    }

    fun submitMap(
        map: Map<AdapterItem.BookItem, List<AdapterItem.BookmarkItem>>,
        commitCallback: (() -> Unit)? = null
    ) {
        val arrayList = arrayListOf<AdapterItem>()

        map.forEach { (bookItem, bookmarkItems) ->
            arrayList.add(bookItem)
            arrayList.addAll(bookmarkItems)
        }

        submitList(arrayList) {
            commitCallback?.invoke()
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = when(viewType) {
            ViewType.BOOK -> BookItemBinding.inflate(inflater, parent, false)
            ViewType.BOOKMARK -> BookmarkItemBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("IllegalArgumentException :$viewType")
        }

        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is AdapterItem.BookItem -> ViewType.BOOK
            is AdapterItem.BookmarkItem -> ViewType.BOOKMARK
        }
    }

    inner class ViewHolder(private val viewBinding: ViewBinding): RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: AdapterItem) {
            when(item) {
                is AdapterItem.BookItem -> {
                    with(viewBinding) {
                        if (this is BookItemBinding) {
                            bind(item)
                        }
                    }
                }
                is AdapterItem.BookmarkItem -> {
                    with(viewBinding) {
                        if (this is BookmarkItemBinding) {
                            bind(item)
                        }
                    }
                }
            }
        }

        private fun BookItemBinding.bind(item: AdapterItem.BookItem) {
            root.text = item.name
        }

        private fun BookmarkItemBinding.bind(item: AdapterItem.BookmarkItem) {
            val verse = item.verse

            val text = String.format(format, bible.name(verse.book), verse.chapter, verse.verse)

            textView.text = text

            with(imageViewBookmark) {
                setOnClickListener {
                    setTint(R.color.unbookmarked)
                    onItemClickListener?.onIconClick(item)
                }
            }

            root.setOnClickListener {
                onItemClickListener?.onItemClick(item)
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

        data class BookItem (
            override val id: Int = -1,
            val name: String,
        ) : AdapterItem()

        data class BookmarkItem(
            override val id: Int,
            val verse: Verse
        ) : AdapterItem()
    }
}