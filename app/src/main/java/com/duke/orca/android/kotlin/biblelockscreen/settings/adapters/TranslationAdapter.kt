package com.duke.orca.android.kotlin.biblelockscreen.settings.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.duke.orca.android.kotlin.biblelockscreen.application.hide
import com.duke.orca.android.kotlin.biblelockscreen.application.show
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.datamodels.Translation.Language.toDisplayName
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.datamodels.Translation.Model
import com.duke.orca.android.kotlin.biblelockscreen.databinding.TranslationItemBinding

class TranslationAdapter(
    private val context: Context,
    private val onCheckedChanged: (Model, Boolean) -> Unit
) : ListAdapter<TranslationAdapter.AdapterItem, TranslationAdapter.ViewHolder>(DiffCallback()) {
    private val layoutInflater by lazy { LayoutInflater.from(context) }

    fun submitItems(items: List<Model>) {
        val arrayList = arrayListOf<AdapterItem>()
        val map = items.groupBy { it.language }

        map.keys.forEach { language ->
            arrayList.add(AdapterItem.Language(language.toDisplayName()))

            map[language]?.forEach { translation ->
                arrayList.add(AdapterItem.Translation(translation.name, translation))
            }
        }

        submitList(arrayList)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        recyclerView.itemAnimator.also {
            if (it is SimpleItemAnimator) {
                it.supportsChangeAnimations = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = TranslationItemBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val viewBinding: TranslationItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: AdapterItem) {
            when(item) {
                is AdapterItem.Language -> {
                    viewBinding.textViewTranslation.hide()
                    viewBinding.textViewLanguage.show()
                    viewBinding.textViewLanguage.text = item.text

                    viewBinding.materialCheckBox.hide()

                    viewBinding.root.setOnClickListener(null)
                }
                is AdapterItem.Translation -> {
                    viewBinding.textViewLanguage.hide()
                    viewBinding.textViewTranslation.show()
                    viewBinding.textViewTranslation.text = item.model.name

                    viewBinding.materialCheckBox.isChecked = item.model.isSelected

                    viewBinding.materialCheckBox.show()
                    viewBinding.materialCheckBox.setOnCheckedChangeListener { compoundButton, isChecked ->
                        if (compoundButton.isPressed) {
                            onCheckedChanged(item.model, isChecked)
                        }
                    }
                }
            }
        }
    }

    sealed class AdapterItem {
        abstract val text: String

        data class Language(
            override val text: String,
        ) : AdapterItem()

        data class Translation(
            override val text: String,
            val model: Model
        ) : AdapterItem()
    }

    class DiffCallback: DiffUtil.ItemCallback<AdapterItem>() {
        override fun areItemsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
            return oldItem.text == newItem.text
        }

        override fun areContentsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
            return oldItem == newItem
        }
    }
}