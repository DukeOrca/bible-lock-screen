package com.duke.orca.android.kotlin.biblelockscreen.settings.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duke.orca.android.kotlin.biblelockscreen.application.hide
import com.duke.orca.android.kotlin.biblelockscreen.application.show
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Translation
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Translation.Companion.Language.toDisplayName
import com.duke.orca.android.kotlin.biblelockscreen.databinding.TranslationSelectionItemBinding

class TranslationSelectionAdapter(
    private val context: Context,
    private val onItemSelected: (Translation) -> Unit
) : ListAdapter<TranslationSelection.AdapterItem, TranslationSelectionAdapter.ViewHolder>(TranslationSelection.DiffCallback()) {
    private val layoutInflater by lazy { LayoutInflater.from(context) }

    fun submitTranslations(list: List<Translation>) {
        val arrayList = arrayListOf<TranslationSelection.AdapterItem>()
        val map = list.groupBy { it.language }

        map.keys.forEach { language ->
            arrayList.add(TranslationSelection.AdapterItem.Language(language.toDisplayName()))

            map[language]?.forEach { translation ->
                arrayList.add(TranslationSelection.AdapterItem.TranslationWrapper(translation.displayName, translation))
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
                    viewBinding.textViewLanguage.text = item.text

                    viewBinding.root.setOnClickListener(null)
                }
                is TranslationSelection.AdapterItem.TranslationWrapper -> {
                    viewBinding.textViewLanguage.hide()
                    viewBinding.textViewTranslation.show()
                    viewBinding.textViewTranslation.text = item.translation.displayName

                    viewBinding.root.setOnClickListener {
                        onItemSelected(item.translation)
                    }
                }
            }
        }
    }
}

object TranslationSelection {
    sealed class AdapterItem {
        abstract val text: String

        data class Language(
            override val text: String,
        ) : AdapterItem()

        data class TranslationWrapper(
            override val text: String,
            val translation: Translation
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