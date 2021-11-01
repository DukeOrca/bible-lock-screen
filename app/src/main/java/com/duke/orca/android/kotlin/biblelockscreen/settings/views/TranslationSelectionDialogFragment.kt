package com.duke.orca.android.kotlin.biblelockscreen.settings.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseDialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.Translation
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentTranslationSelectionDialogBinding
import com.duke.orca.android.kotlin.biblelockscreen.settings.adapter.TranslationSelection
import com.duke.orca.android.kotlin.biblelockscreen.settings.adapter.TranslationSelectionAdapter

class TranslationSelectionDialogFragment : BaseDialogFragment<FragmentTranslationSelectionDialogBinding>() {
    override val setWindowAnimation: Boolean
        get() = false

    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTranslationSelectionDialogBinding {
        return FragmentTranslationSelectionDialogBinding.inflate(inflater, container, false)
    }

    private object Language {
        const val ENGLISH = "English"
        const val FRENCH = "Français"
        const val GERMAN = "Deutsch"
        const val ITALIAN = "Italiano"
        const val KOREAN = "한국어"
    }

    private var onTranslationSelectedListener: OnTranslationSelectedListener? = null

    interface OnTranslationSelectedListener {
        fun onTranslationSelected(
            dialogFragment: TranslationSelectionDialogFragment,
            item: TranslationSelection.AdapterItem.Translation
        )
    }

    private val translationSelectionAdapter by lazy {
        TranslationSelectionAdapter(requireContext()) {
            onTranslationSelectedListener?.onTranslationSelected(this, it)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        with(parentFragment) {
            if (this is OnTranslationSelectedListener) {
                onTranslationSelectedListener = this
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        translationSelectionAdapter.submitHashMap(createHashMap())

        viewBinding.recyclerView.apply {
            adapter = translationSelectionAdapter
            layoutManager = LinearLayoutManagerWrapper(context)
            setHasFixedSize(true)
        }

        return viewBinding.root
    }

    override fun onDetach() {
        onTranslationSelectedListener = null
        super.onDetach()
    }

    private fun createHashMap(): HashMap<TranslationSelection.AdapterItem.Language, Array<TranslationSelection.AdapterItem.Translation>> {
        return linkedMapOf(
            TranslationSelection.AdapterItem.Language(name = Language.ENGLISH) to arrayOf(
                TranslationSelection.AdapterItem.Translation(
                    name = Translation.Name.KING_JAMES_VERSION,
                    displayName = Translation.DisplayName.KING_JAMES_VERSION
                )
            ),
            TranslationSelection.AdapterItem.Language(name = Language.KOREAN) to arrayOf(
                TranslationSelection.AdapterItem.Translation(
                    name = Translation.Name.KOREAN_REVISED_VERSION,
                    displayName = Translation.DisplayName.KOREAN_REVISED_VERSION
                ),
                TranslationSelection.AdapterItem.Translation(
                    name = Translation.Name.NEW_KOREAN_REVISED_VERSION,
                    displayName = Translation.DisplayName.NEW_KOREAN_REVISED_VERSION
                )
            )
        )
    }
}