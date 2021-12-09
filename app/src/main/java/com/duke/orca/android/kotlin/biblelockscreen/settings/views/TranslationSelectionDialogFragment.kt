package com.duke.orca.android.kotlin.biblelockscreen.settings.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.BLANK
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.not
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseDialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.datamodels.Translation
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentTranslationSelectionDialogBinding
import com.duke.orca.android.kotlin.biblelockscreen.settings.adapters.SelectedTranslationAdapter
import com.duke.orca.android.kotlin.biblelockscreen.settings.adapters.TranslationAdapter
import com.duke.orca.android.kotlin.biblelockscreen.settings.viewmodels.TranslationSelectionDialogViewModel

class TranslationSelectionDialogFragment : BaseDialogFragment<FragmentTranslationSelectionDialogBinding>() {
    override val setWindowAnimation: Boolean
        get() = false

    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTranslationSelectionDialogBinding {
        return FragmentTranslationSelectionDialogBinding.inflate(inflater, container, false)
    }

    private val viewModel by viewModels<TranslationSelectionDialogViewModel>()

    private var onClickListener: OnClickListener? = null

    interface OnClickListener {
        fun onNegativeButtonClick(dialogFragment: DialogFragment)

        fun onPositiveButtonClick(
            dialogFragment: DialogFragment,
            translation: Translation.Model,
            subTranslation: Translation.Model?,
            isTranslationChanged: Boolean,
            isSubTranslationChanged: Boolean
        )
    }

    private val selectedTranslationAdapter by lazy {
        SelectedTranslationAdapter(requireContext()) { from, to ->
            viewModel.swap(from, to)
        }
    }

    private val translationAdapter by lazy {
        TranslationAdapter(requireContext()) { item, isChecked ->
            if (isChecked) {
                viewModel.select(item)
            } else {
                viewModel.unselect(item)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        parentFragment?.let {
            if (it is OnClickListener) {
                onClickListener = it
            }
        } ?: run {
            with(context) {
                if (this is OnClickListener) {
                    onClickListener = this
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        bind()

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleCallback?.onDialogFragmentViewCreated(TAG)
        observe()
    }

    override fun onDestroyView() {
        lifecycleCallback?.onDialogFragmentViewDestroyed(TAG)
        super.onDestroyView()
    }

    override fun onDetach() {
        onClickListener = null
        super.onDetach()
    }

    private fun bind() {
        viewBinding.recyclerViewSelected.apply {
            adapter = selectedTranslationAdapter
            layoutManager = LinearLayoutManagerWrapper(context)
            setHasFixedSize(true)
        }

        viewBinding.recyclerView.apply {
            adapter = translationAdapter
            layoutManager = LinearLayoutManagerWrapper(context)
            setHasFixedSize(true)
        }

        viewBinding.textViewCancel.setOnClickListener {
            delayOnLifecycle(Duration.Delay.DISMISS) {
                onClickListener?.onNegativeButtonClick(this)
            }
        }

        viewBinding.textViewOk.setOnClickListener {
            val translation = selectedTranslationAdapter.currentList[0]
            val subTranslation =
                if (viewModel.selectedItemCount > 1) {
                    selectedTranslationAdapter.currentList[1]
                } else {
                    null
                }

            onClickListener?.onPositiveButtonClick(
                this,
                translation,
                subTranslation,
                viewModel.currentFileName.not(translation.fileName),
                viewModel.currentSubFileName.not(subTranslation?.fileName ?: BLANK)
            )
        }
    }

    private fun observe() {
        viewModel.items.observe(viewLifecycleOwner, {
            translationAdapter.submitItems(it)
        })

        viewModel.selectedItems.observe(viewLifecycleOwner, {
            selectedTranslationAdapter.submitList(it)

            viewBinding.textViewOk.isEnabled = it.count() > 0
        })
    }

    companion object {
        const val TAG = "TranslationSelectionDialogFragment"
    }
}