package com.duke.orca.android.kotlin.biblelockscreen.settings.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.EXTRA_RECREATE
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.getVersionName
import com.duke.orca.android.kotlin.biblelockscreen.application.not
import com.duke.orca.android.kotlin.biblelockscreen.application.shareApplication
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.viewmodels.FragmentContainerViewModel
import com.duke.orca.android.kotlin.biblelockscreen.base.views.PreferenceFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Translation
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.review.Review
import com.duke.orca.android.kotlin.biblelockscreen.settings.adapter.AdapterItem

class SettingsFragment : PreferenceFragment(),
    TranslationSelectionDialogFragment.OnTranslationSelectedListener {
    override val toolbar by lazy { viewBinding.toolbar }
    override val toolbarTitleResId: Int = R.string.settings

    private object Id {
        const val TRANSLATION = 0L
    }

    private val activityViewModel by activityViewModels<FragmentContainerViewModel>()

    override fun onTranslationSelected(
        dialogFragment: TranslationSelectionDialogFragment,
        item: Translation
    ) {
        val transition = DataStore.Translation.getFileName(requireContext())

        if (transition.not(item.fileName)) {
            activityViewModel.setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_RECREATE, true))
            DataStore.Translation.putFileName(requireContext(), item.fileName)
            preferenceAdapter.updateSummary(Id.TRANSLATION, item.displayName)
        }

        delayOnLifecycle(Duration.Delay.DISMISS) {
            dialogFragment.dismiss()
        }
    }

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
        preferenceAdapter.submitList(
            arrayListOf(
                AdapterItem.Preference(
                    drawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_round_stay_primary_portrait_24
                    ),
                    summary = getString(R.string.dark_mode),
                    onClick = {
                        Intent(requireContext(), DisplaySettingsActivity::class.java).also {
                            it.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                            startActivity(it)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.z_adjustment_bottom)
                        }
                    },
                    body = getString(R.string.display)
                ),
                AdapterItem.Preference(
                    drawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_round_screen_lock_portrait_24
                    ),
                    summary = "${getString(R.string.lock_setting)}, ${getString(R.string.lock_type)}",
                    onClick = {
                        addFragment(LockScreenSettingsFragment())
                    },
                    body = getString(R.string.lock_screen)
                ),
                AdapterItem.Preference(
                    drawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_typography_48px
                    ),
                    onClick = {
                        addFragment(FontSettingsFragment())
                    },
                    summary = "${getString(R.string.bold)}," +
                            " ${getString(R.string.font_size)}," +
                            " ${getString(R.string.text_alignment)}"
                    ,
                    body = getString(R.string.font)
                ),
                AdapterItem.Space(),
                AdapterItem.Preference(
                    id = Id.TRANSLATION,
                    drawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_round_translate_24
                    ),
                    onClick = {
                        TranslationSelectionDialogFragment().also {
                            it.show(childFragmentManager, it.tag)
                        }
                    },
                    summary = Translation.getDisplayName(requireContext()),
                    body = getString(R.string.translations)
                ),
                AdapterItem.Space(),
                AdapterItem.ContentPreference(
                    drawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_round_share_24
                    ),
                    onClick = {
                        shareApplication(requireContext())
                    },
                    body = getString(R.string.share_the_app)
                ),
                AdapterItem.ContentPreference(
                    drawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_round_rate_review_24
                    ),
                    onClick = {
                        Review.launchReviewFlow(requireActivity())
                    },
                    body = getString(R.string.write_review)
                ),
                AdapterItem.ContentPreference(
                    drawable = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_round_info_24
                    ),
                    isClickable = false,
                    onClick = {

                    },
                    summary = getVersionName(requireContext()),
                    body = getString(R.string.version)
                )
            )
        )
    }

    private fun bind() {
        viewBinding.recyclerView.apply {
            adapter = preferenceAdapter
            layoutManager = LinearLayoutManagerWrapper(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun addFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .add(R.id.fragment_container_view, fragment, fragment.tag)
            .addToBackStack(null)
            .commit()
    }
}