package com.duke.orca.android.kotlin.biblelockscreen.bible.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.datamodels.Highlight
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Verse
import com.duke.orca.android.kotlin.biblelockscreen.databinding.HighlightItemBinding
import timber.log.Timber

class HighlightAdapter(
    private val onItemSelected: (selectedItem: Highlight) -> Unit
) : ListAdapter<Highlight, HighlightAdapter.ViewHolder>(DiffCallback()) {
    private var selectedItem: Highlight? = null

    inner class ViewHolder(private val viewBinding: HighlightItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: Highlight) {
            with(viewBinding) {
                frameLayout.backgroundTintList = ColorStateList.valueOf(item.highlightColor)
                textView.text = "${item.verses.count()}"

                root.setOnClickListener {
                    selectedItem = item
                    onItemSelected.invoke(item)
                }
            }
        }
    }

    fun select(position: Int) {
        try {
            with(getItem(position)) {
                selectedItem = this
                onItemSelected.invoke(this)
            }
        } catch (e: IndexOutOfBoundsException) {
            Timber.e(e)
        }
    }

    fun submitMap(map: Map<Int, List<Verse>>, commitCallback: (() -> Unit)? = null) {
        val arrayList = arrayListOf<Highlight>()

        map.forEach { (key, value) ->
            arrayList.add(Highlight(key, value))
        }

        submitList(arrayList) {
            commitCallback?.invoke()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewBinding = HighlightItemBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback: DiffUtil.ItemCallback<Highlight>() {
        override fun areItemsTheSame(oldItem: Highlight, newItem: Highlight): Boolean {
            return oldItem.highlightColor == newItem.highlightColor
        }

        override fun areContentsTheSame(oldItem: Highlight, newItem: Highlight): Boolean {
            return oldItem == newItem
        }
    }
}