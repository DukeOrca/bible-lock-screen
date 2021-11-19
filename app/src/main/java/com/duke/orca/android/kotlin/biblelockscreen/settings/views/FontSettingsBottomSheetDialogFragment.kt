package com.duke.orca.android.kotlin.biblelockscreen.settings.views

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentFontSettingsBottomSheetDialogBinding
import com.duke.orca.android.kotlin.biblelockscreen.settings.viewmodels.FontSettingsBottomSheetDialogViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
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

        bind()

        return viewBinding.root
    }

    override fun onDestroyView() {
        _viewBinding = null
        super.onDestroyView()
    }

    private fun bind() {
        lifecycleScope.launch {
            viewBinding.textViewResult.text = viewModel.verse(0)?.word
        }

        viewBinding.slider.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                val text = "${value}.dp"

                viewBinding.textViewFontSizeSummary.text = text
                viewBinding.textViewResult.setTextSize(TypedValue.COMPLEX_UNIT_DIP, value)
            }
        }
    }
}