package com.duke.orca.android.kotlin.biblelockscreen.settings.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duke.orca.android.kotlin.biblelockscreen.application.hide
import com.duke.orca.android.kotlin.biblelockscreen.application.show
import com.duke.orca.android.kotlin.biblelockscreen.databinding.TranslationSelectionItemBinding

class TranslationSelectionAdapter(
    private val context: Context,
    private val onItemSelected: (TranslationSelection.AdapterItem.Translation) -> Unit
) : ListAdapter<TranslationSelection.AdapterItem, TranslationSelectionAdapter.ViewHolder>(TranslationSelection.DiffCallback()) {
    private val layoutInflater by lazy { LayoutInflater.from(context) }

    fun submitHashMap(hashMap: HashMap<TranslationSelection.AdapterItem.Language, Array<TranslationSelection.AdapterItem.Translation>>) {
        val arrayList = arrayListOf<TranslationSelection.AdapterItem>()

        hashMap.keys.forEach { language ->
            arrayList.add(language)

            hashMap[language]?.forEach { translation ->
                arrayList.add(translation)
            }
        }

        submitList(arrayList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = TranslationSelectionItemBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val viewBinding: TranslationSelectionItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: TranslationSelection.AdapterItem) {
            when(item) {
                is TranslationSelection.AdapterItem.Language -> {
                    viewBinding.textViewTranslation.hide()
                    viewBinding.textViewLanguage.show()
                    viewBinding.textViewLanguage.text = item.name

                    viewBinding.root.setOnClickListener(null)
                }
                is TranslationSelection.AdapterItem.Translation -> {
                    viewBinding.textViewLanguage.hide()
                    viewBinding.textViewTranslation.show()
                    viewBinding.textViewTranslation.text = item.displayName

                    viewBinding.root.setOnClickListener {
                        onItemSelected(item)
                    }
                }
            }
        }
    }
}

object TranslationSelection {
    sealed class AdapterItem {
        abstract val name: String

        data class Language(
            override val name: String,
        ) : AdapterItem()

        data class Translation(
            override val name: String,
            val displayName: String
        ) : AdapterItem()
    }

    class DiffCallback: DiffUtil.ItemCallback<AdapterItem>() {
        override fun areItemsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
            return oldItem == newItem
        }
    }
}