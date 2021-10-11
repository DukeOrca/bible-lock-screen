package com.duke.orca.android.kotlin.biblelockscreen.bible.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.setTint
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.databinding.BibleChapterBinding

class BibleChapterAdapter(context: Context) : ListAdapter<BibleChapter, BibleChapterAdapter.ViewHolder>(DiffCallback()) {
    private val books = context.resources.getStringArray(R.array.books)
    private val format = context.getString(R.string.format_bible_chapter)
    private val inflater = LayoutInflater.from(context)

    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(item: BibleChapter)
        fun onIconClick(item: BibleChapter)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return ViewHolder(BibleChapterBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val viewBinding: BibleChapterBinding): RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: BibleChapter) {
            val text = String.format(format, books[item.book.dec()], item.chapter)

            viewBinding.textView.text = text

            with(viewBinding.imageViewBookmark) {
                setOnClickListener {
                    this.setTint(R.color.unbookmarked)
                    onItemClickListener?.onIconClick(item)
                }
            }

            viewBinding.root.setOnClickListener {
                onItemClickListener?.onItemClick(item)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<BibleChapter>() {
        override fun areItemsTheSame(oldItem: BibleChapter, newItem: BibleChapter): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BibleChapter, newItem: BibleChapter): Boolean {
            return oldItem == newItem
        }
    }
}