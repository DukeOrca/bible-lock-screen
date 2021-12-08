package com.duke.orca.android.kotlin.biblelockscreen.settings.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.PreferenceFragment
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.settings.adapters.PreferenceAdapter.AdapterItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DisplaySettingsFragment : PreferenceFragment() {
    override val toolbar by lazy { viewBinding.toolbar }
    override val toolbarTitleResId: Int = R.string.display

    private val items = arrayListOf<AdapterItem>()

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
        val isDarkMode = DataStore.Display.isDarkMode(requireContext())

        with(items) {
            add(
                AdapterItem.SwitchPreference(
                    drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_dark_mode_24),
                    isChecked = isDarkMode,
                    onCheckedChange = { isChecked ->
                        lifecycleScope.launch {
                            DataStore.Display.putDarkMode(requireContext(), isChecked)
                            delay(Duration.Delay.RECREATE)
                            activity?.recreate()
                        }
                    },
                    body = getString(R.string.dark_mode)
                )
            )
        }

        preferenceAdapter.submitList(items.toList())
    }

    private fun bind() {
        viewBinding.recyclerView.apply {
            adapter = preferenceAdapter
            layoutManager = LinearLayoutManagerWrapper(requireContext())
            setHasFixedSize(true)
        }
    }
}