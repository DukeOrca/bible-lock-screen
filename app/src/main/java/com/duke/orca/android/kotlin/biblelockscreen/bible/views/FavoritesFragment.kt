package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isInvisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeIn
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseChildFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.VerseAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.copyToClipboard
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Verse
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.ChapterVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.share
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels.FavoritesViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentFavoritesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : BaseChildFragment<FragmentFavoritesBinding>(),
    VerseAdapter.OnIconClickListener,
    OptionChoiceDialogFragment.OnOptionChoiceListener {
    override val toolbar: Toolbar by lazy { viewBinding.toolbar }

    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoritesBinding {
        return FragmentFavoritesBinding.inflate(inflater, container, false)
    }

    private val viewModel by viewModels<FavoritesViewModel>()
    private val verseAdapter by lazy {
        VerseAdapter(viewModel.bibleBook) {
            val chapterVerse = ChapterVerse(it.chapter.dec(), it.verse)

            addFragment(
                R.id.fragment_container_view,
                parentFragmentManager,
                ChapterPagerFragment.newInstance(chapterVerse)
            )
        }
    }
    private val options by lazy { arrayOf(getString(R.string.copy), getString(R.string.share)) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        observe()
        bind()
        viewModel.getFavorites()

        return viewBinding.root
    }

    private fun observe() {
        viewModel.adapterItems.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                viewBinding.linearLayout.fadeIn(Duration.FADE_IN)
            }

            verseAdapter.submitList(it) {
                if (viewBinding.recyclerView.isInvisible) {
                    delayOnLifecycle(Duration.Delay.DISMISS) {
                        viewBinding.recyclerView.fadeIn(Duration.FADE_IN)
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

    override fun onFavoriteClick(verse: Verse, favorites: Boolean) {
        viewModel.updateFavorites(verse.id, favorites)
    }

    override fun onMoreVertClick(verse: Verse) {
        OptionChoiceDialogFragment.newInstance(options, verse).also {
            it.show(childFragmentManager, it.tag)
        }
    }

    override fun onOptionChoice(
        dialogFragment: DialogFragment,
        option: String,
        verse: Verse?
    ) {
        when(option) {
            options[0] -> {
                verse?.let { copyToClipboard(requireContext(), viewModel.bibleBook, it) }
                delayOnLifecycle(Duration.Delay.DISMISS) {
                    dialogFragment.dismiss()
                }
            }
            options[1] -> {
                verse?.let { share(requireContext(), viewModel.bibleBook, it) }
                delayOnLifecycle(Duration.Delay.DISMISS) {
                    dialogFragment.dismiss()
                }
            }
        }
    }
}