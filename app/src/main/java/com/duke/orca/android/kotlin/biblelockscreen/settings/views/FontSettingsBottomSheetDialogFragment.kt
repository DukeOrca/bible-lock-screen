package com.duke.orca.android.kotlin.biblelockscreen.settings.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentFontSettingsBottomSheetDialogBinding
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.settings.viewmodels.FontSettingsBottomSheetDialogViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.Slider

class FontSettingsBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private var _viewBinding: FragmentFontSettingsBottomSheetDialogBinding? = null
    val viewBinding: FragmentFontSettingsBottomSheetDialogBinding
        get() = _viewBinding!!

    private val viewModel by viewModels<FontSettingsBottomSheetDialogViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = FragmentFontSettingsBottomSheetDialogBinding.inflate(inflater, container, false)

        initData()
        bind()

        return viewBinding.root
    }

    override fun onDestroyView() {
        _viewBinding = null
        super.onDestroyView()
    }

    private fun initData() {
        val font = DataStore.Font.Bible.getSize(requireContext())
        val textAlignment = DataStore.Font.Bible.getTextAlignment(requireContext())

        with(viewBinding) {
            val text = "${font}dp"

            textViewFontSizeSummary.text = text
            slider.value = font

            val id = when(textAlignment) {
                DataStore.Font.TextAlignment.LEFT -> R.id.material_button_align_left
                DataStore.Font.TextAlignment.CENTER -> R.id.material_button_align_center
                DataStore.Font.TextAlignment.RIGHT -> R.id.material_button_align_right
                else -> R.id.material_button_align_left
            }

            materialButtonToggleGroup.check(id)
        }
    }

    private fun bind() {
        viewBinding.slider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                val text = "${value}dp"

                viewBinding.textViewFontSizeSummary.text = text
            }
        }

        viewBinding.slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
            }

            override fun onStopTrackingTouch(slider: Slider) {
                viewModel.putSize(slider.value)
            }
        })

        viewBinding.materialButtonToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val textAlignment = when(checkedId) {
                R.id.material_button_align_left -> DataStore.Font.TextAlignment.LEFT
                R.id.material_button_align_center -> DataStore.Font.TextAlignment.CENTER
                R.id.material_button_align_right -> DataStore.Font.TextAlignment.RIGHT
                else -> DataStore.Font.TextAlignment.LEFT
            }

            viewModel.putTextAlignment(textAlignment)
        }
    }
}