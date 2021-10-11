package com.duke.orca.android.kotlin.biblelockscreen.settings.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.Duration
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.PreferenceChildFragment
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.settings.adapter.AdapterItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DisplaySettingsFragment : PreferenceChildFragment() {
    override val toolbar by lazy { viewBinding.toolbar }
    override val toolbarTitleResId: Int = R.string.display

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

        val list: List<AdapterItem> = arrayListOf(
            AdapterItem.SwitchPreference(
                drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_round_dark_mode_24),
                isChecked = isDarkMode,
                onCheckedChange = { isChecked ->
                    lifecycleScope.launch {
                        val darkMode = if (isChecked) {
                            AppCompatDelegate.MODE_NIGHT_YES
                        } else {
                            AppCompatDelegate.MODE_NIGHT_NO
                        }

                        withContext(Dispatchers.IO) {
                            DataStore.Display.putDarkMode(requireContext(), isChecked)
                            delay(Duration.MEDIUM)
                        }

                        AppCompatDelegate.setDefaultNightMode(darkMode)
                    }
                },
                title = getString(R.string.dark_mode)
            )
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