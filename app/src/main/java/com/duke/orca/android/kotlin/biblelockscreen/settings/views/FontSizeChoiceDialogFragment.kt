package com.duke.orca.android.kotlin.biblelockscreen.settings.views

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.DialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.base.views.SingleChoiceDialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.databinding.SingleChoiceItemBinding

class FontSizeChoiceDialogFragment : SingleChoiceDialogFragment<Float>() {
    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(dialogFragment: DialogFragment, item: Float)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragment?.let {
            if (it is OnItemClickListener) {
                this.onItemClickListener = it
            }
        }
    }

    override val items: Array<Float> = arrayOf(12F, 14F, 16F, 20F, 24F, 32F)

    override fun bind(viewBinding: SingleChoiceItemBinding, item: Float) {
        val text = "${item}dp"
        viewBinding.root.text = text
        viewBinding.root.setTextSize(TypedValue.COMPLEX_UNIT_DIP, item)
        viewBinding.root.setOnClickListener {
            onItemClickListener?.onItemClick(this, item)
        }
    }
}