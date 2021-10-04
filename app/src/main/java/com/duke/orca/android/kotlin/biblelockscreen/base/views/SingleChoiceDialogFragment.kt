package com.duke.orca.android.kotlin.biblelockscreen.base.views

import android.view.LayoutInflater
import android.view.ViewGroup
import com.duke.orca.android.kotlin.biblelockscreen.databinding.SingleChoiceItemBinding

abstract class SingleChoiceDialogFragment<T> : BaseListDialogFragment<SingleChoiceItemBinding, T>() {
    override fun inflateItemView(
        inflater: LayoutInflater,
        container: ViewGroup
    ): SingleChoiceItemBinding {
        return SingleChoiceItemBinding.inflate(inflater, container, false)
    }
}