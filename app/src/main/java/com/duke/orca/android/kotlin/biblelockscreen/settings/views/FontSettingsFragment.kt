package com.duke.orca.android.kotlin.biblelockscreen.settings.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.PreferenceChildFragment
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.settings.adapter.AdapterItem

class FontSettingsFragment : PreferenceChildFragment() {
    private object Id {
        const val FONT_SIZE = 0L
    }

    override val toolbar by lazy { viewBinding.toolbar }
    override val toolbarTitleResId: Int = R.string.lock_screen

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        initData()
        bind()

        return viewBinding.root
    }

    private fun initData() {
        val fontSize = DataStore.Display.getFontSize(requireContext())

        val list: List<AdapterItem> = arrayListOf(
            AdapterItem.Preference(
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_format_size_24),
                id = Id.FONT_SIZE,
                summary = "${fontSize}dp",
                onClick = {
                    FontSizeChoiceDialogFragment().also {
                        it.show(childFragmentManager, it.tag)
                    }
                },
                title = getString(R.string.font_size)
            ),
        )

        preferenceAdapter.submitList(list)
    }

    private fun bind() {
        viewBinding.recyclerView.apply {
            adapter = preferenceAdapter
            layoutManager = LinearLayoutManagerWrapper(requireContext())
            setHasFixedSize(true)
        }
    }
}