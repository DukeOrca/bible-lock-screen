package com.duke.orca.android.kotlin.biblelockscreen.settings.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.getVersionName
import com.duke.orca.android.kotlin.biblelockscreen.application.shareApplication
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.PreferenceFragment
import com.duke.orca.android.kotlin.biblelockscreen.review.Review
import com.duke.orca.android.kotlin.biblelockscreen.settings.adapter.AdapterItem

class SettingsFragment : PreferenceFragment() {
    override val changeSystemUiColor: Boolean = true
    override val toolbar by lazy { viewBinding.toolbar }
    override val toolbarTitleResId: Int = R.string.settings

    private var onBackPressedTimeMillis = 0L

    private val onBackPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - onBackPressedTimeMillis >= Duration.LONG) {
                    onBackPressedTimeMillis = System.currentTimeMillis()
                    with(childFragmentManager) {
                        if (backStackEntryCount < 1) {
                            parentFragmentManager.popBackStackImmediate()
                        } else {
                            popBackStackImmediate()
                        }
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
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

    override fun onDetach() {
        onBackPressedCallback.remove()
        super.onDetach()
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
                        addFragment(DisplaySettingsFragment())
                    },
                    title = getString(R.string.display)
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
                    title = getString(R.string.font)
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
                    title = getString(R.string.lock_screen)
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
        childFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .setReorderingAllowed(true)
            .add(R.id.fragment_container_view, fragment, fragment.tag)
            .addToBackStack(null)
            .commit()
    }
}