package com.duke.orca.android.kotlin.biblelockscreen.settings.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.ItemTouchCallback
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Translation
import com.duke.orca.android.kotlin.biblelockscreen.databinding.SelectedTranslationItemBinding
import java.util.*

class SelectedTranslationAdapter(
    context: Context,
    private val onItemMoved: (from: Int, to: Int) -> Unit
) : ListAdapter<Translation.Model, SelectedTranslationAdapter.ViewHolder>(DiffCallback()) {
    private val itemTouchHelper by lazy {
        ItemTouchHelper(ItemTouchCallback(this))
    }

    private val layoutInflater = LayoutInflater.from(context)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SelectedTranslationItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(private val viewBinding: SelectedTranslationItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(position: Int) {
            val item = getItem(position)

            viewBinding.textViewTranslation.text = item.name

            viewBinding.imageViewSort.setOnTouchListener { _, event ->
                when(event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        itemTouchHelper.startDrag(this)
                        return@setOnTouchListener true
                    }
                }

                false
            }
        }
    }

    fun onItemMove(from: Int, to: Int) {
        if (currentList.count() <= from || currentList.count() <= to)
            return

        onItemMoved(from, to)
    }

    class DiffCallback: DiffUtil.ItemCallback<Translation.Model>() {
        override fun areItemsTheSame(oldItem: Translation.Model, newItem: Translation.Model): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Translation.Model, newItem: Translation.Model): Boolean {
            return oldItem == newItem
        }
    }
}