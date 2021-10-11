package com.duke.orca.android.kotlin.biblelockscreen.settings.views

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseListDialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.databinding.TextAlignmentChoiceItemBinding
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore

class TextAlignmentChoiceDialogFragment : BaseListDialogFragment<TextAlignmentChoiceItemBinding, Int>() {
    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(dialogFragment: DialogFragment, item: Int)
    }

    override fun inflateItemView(
        inflater: LayoutInflater,
        container: ViewGroup
    ): TextAlignmentChoiceItemBinding {
        return TextAlignmentChoiceItemBinding.inflate(inflater, container, false)
    }

    override fun bind(viewBinding: TextAlignmentChoiceItemBinding, item: Int) {
        viewBinding.imageView.setImageDrawable(getTextAlignmentDrawable(item))
        viewBinding.textView.text = getTextAlignmentString(item)

        viewBinding.root.setOnClickListener {
            onItemClickListener?.onItemClick(this, item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragment?.let {
            if (it is OnItemClickListener) {
                this.onItemClickListener = it
            }
        }
    }

    override val items: Array<Int> = arrayOf(
        DataStore.Font.TextAlignment.CENTER,
        DataStore.Font.TextAlignment.LEFT,
        DataStore.Font.TextAlignment.RIGHT
    )

    private fun getTextAlignmentDrawable(textAlignment: Int): Drawable? {
        @DrawableRes
        val id = when(textAlignment) {
            DataStore.Font.TextAlignment.CENTER -> R.drawable.ic_round_format_align_center_24
            DataStore.Font.TextAlignment.LEFT -> R.drawable.ic_round_format_align_left_24
            DataStore.Font.TextAlignment.RIGHT -> R.drawable.ic_round_format_align_right_24
            else -> throw IllegalArgumentException()
        }

        return ContextCompat.getDrawable(requireContext(), id)
    }

    private fun getTextAlignmentString(textAlignment: Int): String {
        @StringRes
        val id = when(textAlignment) {
            DataStore.Font.TextAlignment.CENTER -> R.string.align_center
            DataStore.Font.TextAlignment.LEFT -> R.string.align_left
            DataStore.Font.TextAlignment.RIGHT -> R.string.align_right
            else -> throw IllegalArgumentException()
        }

        return getString(id)
    }
}