package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Key
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.RequestKey
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeIn
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeOut
import com.duke.orca.android.kotlin.biblelockscreen.application.isNotVisible
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.VerseAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.copyToClipboard
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Verse
import com.duke.orca.android.kotlin.biblelockscreen.bible.share
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels.FavoritesViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentFavoritesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>(),
    VerseAdapter.OnIconClickListener,
    OptionChoiceDialogFragment.OnOptionChoiceListener {
    override val toolbar: Toolbar? by lazy { viewBinding.toolbar }

    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoritesBinding {
        return FragmentFavoritesBinding.inflate(inflater, container, false)
    }

    private val viewModel by viewModels<FavoritesViewModel>()
    private val verseAdapter by lazy {
        VerseAdapter(viewModel.bibleBook) {
            if (fragmentResultSetRequired) {
                setFragmentResult(
                    RequestKey.HIGHLIGHTS_FRAGMENT,
                    bundleOf(Key.VERSE to it)
                )

                parentFragmentManager.popBackStackImmediate()
            } else {
                addFragment(
                    R.id.fragment_container_view,
                    parentFragmentManager,
                    ChapterPagerFragment.newInstance(it)
                )
            }
        }
    }

    private val fragmentResultSetRequired by lazy {
        arguments?.getBoolean(Key.FRAGMENT_RESULT_SET_REQUIRED) ?: false
    }

    private val options by lazy { arrayOf(getString(R.string.copy), getString(R.string.share)) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        bind()
        observe()
    }

    private fun initData() {
        viewModel.loadFavorites()
    }

    private fun observe() {
        viewModel.adapterItems.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                viewBinding.linearLayout.fadeIn(Duration.Animation.FADE_IN)
            } else {
                viewBinding.linearLayout.fadeOut(Duration.Animation.FADE_OUT)
            }

            verseAdapter.submitGroupedList(it) {
                with(viewBinding.recyclerView) {
                    if (isNotVisible) {
                        delayOnLifecycle(Duration.Delay.SLIDE_IN) {
                            fadeIn(Duration.Animation.FADE_IN)
                        }
                    }
                }
            }
        })
    }

    private fun bind() {
        verseAdapter.setOnIconClickListener(this)

        viewBinding.recyclerView.apply {
            adapter = verseAdapter
            layoutManager = LinearLayoutManagerWrapper(context)
            setHasFixedSize(true)
        }
    }

    override fun onBookmarkClick(verse: Verse, bookmark: Boolean) {
        viewModel.updateBookmark(verse.id, bookmark)
    }

    override fun onFavoriteClick(verse: Verse, favorite: Boolean) {
        viewModel.updateFavorites(verse.id, favorite)
    }

    override fun onMoreVertClick(verse: Verse) {
        OptionChoiceDialogFragment.newInstance(options, verse.content).also {
            it.show(childFragmentManager, it.tag)
        }
    }

    override fun onOptionChoice(
        dialogFragment: DialogFragment,
        option: String,
        content: Verse.Content
    ) {
        when(option) {
            options[0] -> {
                copyToClipboard(requireContext(), viewModel.bibleBook, content)
                dialogFragment.dismiss()
            }
            options[1] -> {
                share(requireContext(), viewModel.bibleBook, content)
                dialogFragment.dismiss()
            }
        }
    }

    companion object {
        fun newInstance(fragmentResultSetRequired: Boolean): FavoritesFragment {
            return FavoritesFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(Key.FRAGMENT_RESULT_SET_REQUIRED, fragmentResultSetRequired)
                }
            }
        }
    }
}