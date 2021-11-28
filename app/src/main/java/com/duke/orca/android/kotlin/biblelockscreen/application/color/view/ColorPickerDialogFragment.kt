package com.duke.orca.android.kotlin.biblelockscreen.application.color.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.color.adapter.ColorAdapter
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseDialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentColorPickerDialogBinding
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore

class ColorPickerDialogFragment : BaseDialogFragment<FragmentColorPickerDialogBinding>() {
    override val setWindowAnimation: Boolean
        get() = false

    private val colorAdapter by lazy {
        ColorAdapter(
            resources.getIntArray(R.array.highlight_colors),
            DataStore.HighlightColor.getHighlightColor(requireContext()),
        ) {
            onColorPickedListener?.onColorPicked(this, it)
        }
    }

    private var onColorPickedListener: OnColorPickedListener? = null

    interface OnColorPickedListener {
        fun onColorPicked(dialogFragment: DialogFragment, @ColorInt color: Int)
    }

    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentColorPickerDialogBinding {
        return FragmentColorPickerDialogBinding.inflate(inflater, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        with(parentFragment) {
            if (this is OnColorPickedListener) {
                onColorPickedListener = this
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

    private fun bind() {
        with(viewBinding) {
            recyclerView.apply {
                adapter = colorAdapter
                layoutManager = GridLayoutManager(context, SPAN_COUNT)
            }
        }
    }

    companion object {
        private const val SPAN_COUNT = 4
    }
}