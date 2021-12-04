package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.base.views.SingleChoiceDialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Verse
import com.duke.orca.android.kotlin.biblelockscreen.databinding.SingleChoiceItemBinding

class OptionChoiceDialogFragment : SingleChoiceDialogFragment<String>() {
    interface OnOptionChoiceListener {
        fun onOptionChoice(dialogFragment: DialogFragment, option: String, verse: Verse?)
    }

    private val verse by lazy { arguments?.getParcelable<Verse>(Key.VERSE) }

    private var onOptionChoiceListener: OnOptionChoiceListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragment?.let {
            if (it is OnOptionChoiceListener) {
                this.onOptionChoiceListener = it
            }
        }
    }

    override fun bind(viewBinding: SingleChoiceItemBinding, item: String) {
        viewBinding.root.text = item
        viewBinding.root.setOnClickListener {
            onOptionChoiceListener?.onOptionChoice(this, item, verse)
        }
    }

    override val items: Array<String> by lazy { arguments?.getStringArray(Key.ITEMS) ?: emptyArray() }

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.bibleverses.views"

        private object Key {
            const val ITEMS = "$PACKAGE_NAME.ITEMS"
            const val VERSE = "$PACKAGE_NAME.VERSE"
        }

        fun newInstance(items: Array<String>, verse: Verse): OptionChoiceDialogFragment {
            return OptionChoiceDialogFragment().apply {
                arguments = bundleOf(
                    Key.ITEMS to items,
                    Key.VERSE to verse
                )
            }
        }
    }
}