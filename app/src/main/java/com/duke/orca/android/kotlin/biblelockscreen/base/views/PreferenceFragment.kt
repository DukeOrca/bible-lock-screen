package com.duke.orca.android.kotlin.biblelockscreen.base.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentPreferenceBinding
import com.duke.orca.android.kotlin.biblelockscreen.datastore.preferencesDataStore
import com.duke.orca.android.kotlin.biblelockscreen.settings.adapters.PreferenceAdapter

abstract class PreferenceFragment : BaseChildFragment<FragmentPreferenceBinding>() {
    abstract val toolbarTitleResId: Int

    protected val dataStore by lazy { requireContext().preferencesDataStore }
    protected val preferenceAdapter = PreferenceAdapter()

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentPreferenceBinding {
        return FragmentPreferenceBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        viewBinding.toolbar.setTitle(toolbarTitleResId)

        return viewBinding.root
    }
}