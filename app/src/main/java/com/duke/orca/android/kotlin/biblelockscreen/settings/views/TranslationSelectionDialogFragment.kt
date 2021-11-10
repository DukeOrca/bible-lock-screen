package com.duke.orca.android.kotlin.biblelockscreen.settings.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseDialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Translation
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentTranslationSelectionDialogBinding
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

    private var onTranslationSelectedListener: OnTranslationSelectedListener? = null

    interface OnTranslationSelectedListener {
        fun onTranslationSelected(
            dialogFragment: TranslationSelectionDialogFragment,
            item: Translation
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

        translationSelectionAdapter.submitTranslations(createList())

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

    private fun createList(): List<Translation> {
        return listOf(
            Translation(
                abbreviation = Translation.Companion.Abbreviation.AMERICAN_KING_JAMES_VERSION,
                displayName = Translation.Companion.DisplayName.AMERICAN_KING_JAMES_VERSION,
                fileName = Translation.Companion.FileName.AMERICAN_KING_JAMES_VERSION,
                language = Translation.Companion.Language.ENGLISH
            ),
            Translation(
                abbreviation = Translation.Companion.Abbreviation.AMERICAN_STANDARD_VERSION,
                displayName = Translation.Companion.DisplayName.AMERICAN_STANDARD_VERSION,
                fileName = Translation.Companion.FileName.AMERICAN_STANDARD_VERSION,
                language = Translation.Companion.Language.ENGLISH
            ),
            Translation(
                abbreviation = Translation.Companion.Abbreviation.KING_JAMES_VERSION,
                displayName = Translation.Companion.DisplayName.KING_JAMES_VERSION,
                fileName = Translation.Companion.FileName.KING_JAMES_VERSION,
                language = Translation.Companion.Language.ENGLISH
            ),
            Translation(
                abbreviation = Translation.Companion.Abbreviation.UPDATED_KING_JAMES_VERSION,
                displayName = Translation.Companion.DisplayName.UPDATED_KING_JAMES_VERSION,
                fileName = Translation.Companion.FileName.UPDATED_KING_JAMES_VERSION,
                language = Translation.Companion.Language.ENGLISH
            ),
            Translation(
                abbreviation = Translation.Companion.Abbreviation.LOUIS_SEGOND,
                displayName = Translation.Companion.DisplayName.LOUIS_SEGOND,
                fileName = Translation.Companion.FileName.LOUIS_SEGOND,
                language = Translation.Companion.Language.FRENCH
            ),
            Translation(
                abbreviation = Translation.Companion.Abbreviation.LUTHER_BIBLE,
                displayName = Translation.Companion.DisplayName.LUTHER_BIBLE,
                fileName = Translation.Companion.FileName.LUTHER_BIBLE,
                language = Translation.Companion.Language.GERMAN
            ),
            Translation(
                abbreviation = Translation.Companion.Abbreviation.KOREAN_REVISED_VERSION,
                displayName = Translation.Companion.DisplayName.KOREAN_REVISED_VERSION,
                fileName = Translation.Companion.FileName.KOREAN_REVISED_VERSION,
                language = Translation.Companion.Language.KOREAN
            ),
        )
    }
}