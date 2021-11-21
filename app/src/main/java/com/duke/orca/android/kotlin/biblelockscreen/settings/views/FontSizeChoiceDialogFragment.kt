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

    override val items: Array<Float> = arrayOf(12f, 14f, 16f, 20f, 24f, 32f, 40f)

    override fun bind(viewBinding: SingleChoiceItemBinding, item: Float) {
        val text = String.format("%.0f", item)
        viewBinding.root.text = text
        viewBinding.root.setTextSize(TypedValue.COMPLEX_UNIT_DIP, item)
        viewBinding.root.setOnClickListener {
            onItemClickListener?.onItemClick(this, item)
        }
    }
}