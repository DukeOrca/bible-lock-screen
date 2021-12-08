package com.duke.orca.android.kotlin.biblelockscreen.bible.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.duke.orca.android.kotlin.biblelockscreen.application.`is`
import com.duke.orca.android.kotlin.biblelockscreen.application.hide
import com.duke.orca.android.kotlin.biblelockscreen.application.scale
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.datamodels.Highlight
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Verse
import com.duke.orca.android.kotlin.biblelockscreen.databinding.HighlightItemBinding
import timber.log.Timber

class HighlightAdapter(
    private val onItemSelected: (selectedItem: Highlight) -> Unit
) : ListAdapter<Highlight, HighlightAdapter.ViewHolder>(DiffCallback()) {
    private var selectedItemPosition = -1

    inner class ViewHolder(private val viewBinding: HighlightItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(position: Int) {
            val item = getItem(position)

            with(viewBinding) {
                frameLayout.backgroundTintList = ColorStateList.valueOf(item.highlightColor)
                textView.text = "${item.verses.count()}"

                if (selectedItemPosition.`is`(position)) {
                    imageView.scale(
                        0.5f,
                        1.0f,
                        0.0f,
                        1.0f)
                } else if (imageView.isVisible) {
                    imageView.scale(
                        1.0f,
                        0.5f,
                        1.0f,
                        0.0f) {
                        imageView.hide()
                    }
                }

                root.setOnClickListener {
                    select(position)
                }
            }
        }
    }

    fun select(newPosition: Int) {
        try {
            val oldPosition = selectedItemPosition

            selectedItemPosition = newPosition
            onItemSelected.invoke(getItem(newPosition))

            notifyItemChanged(oldPosition)
            notifyItemChanged(newPosition)
        } catch (e: IndexOutOfBoundsException) {
            Timber.e(e)
        }
    }

    fun submitList(list: List<Verse>, commitCallback: (() -> Unit)? = null) {
        val arrayList = arrayListOf<Highlight>()

        list.groupBy { it.highlightColor }.forEach { (key, value) ->
            arrayList.add(Highlight(key, value))
        }

        submitList(arrayList) {
            commitCallback?.invoke()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        with(recyclerView.itemAnimator) {
            if (this is SimpleItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewBinding = HighlightItemBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
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