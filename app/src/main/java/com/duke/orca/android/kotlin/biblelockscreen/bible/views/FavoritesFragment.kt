package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeIn
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseChildFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.BibleVerseAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.copyToClipboard
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.share
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodel.FavoritesViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentFavoritesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : BaseChildFragment<FragmentFavoritesBinding>(),
    BibleVerseAdapter.OnIconClickListener,
    OptionChoiceDialogFragment.OnOptionChoiceListener {
    override val toolbar: Toolbar by lazy { viewBinding.toolbar }

    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoritesBinding {
        return FragmentFavoritesBinding.inflate(inflater, container, false)
    }

    private val viewModel by viewModels<FavoritesViewModel>()
    private val bibleVerseAdapter by lazy { BibleVerseAdapter(books) }
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
                viewBinding.linearLayout.fadeIn(Duration.MEDIUM)
            }

            bibleVerseAdapter.submitList(it)
        })
    }

    private fun bind() {
        bibleVerseAdapter.setOnIconClickListener(this)

        viewBinding.recyclerView.apply {
            scheduleLayoutAnimation()

            adapter = bibleVerseAdapter
            layoutManager = LinearLayoutManagerWrapper(context)
            setHasFixedSize(true)
        }
    }

    override fun onFavoriteClick(bibleVerse: BibleVerse, favorites: Boolean) {
        viewModel.updateFavorites(bibleVerse.id, favorites)
    }

    override fun onMoreVertClick(bibleVerse: BibleVerse) {
        OptionChoiceDialogFragment.newInstance(options, bibleVerse).also {
            it.show(childFragmentManager, it.tag)
        }
    }

    override fun onOptionChoice(
        dialogFragment: DialogFragment,
        option: String,
        bibleVerse: BibleVerse?
    ) {
        when(option) {
            options[0] -> {
                bibleVerse?.let { copyToClipboard(requireContext(), it) }
                delayOnLifecycle(Duration.Delay.DISMISS) {
                    dialogFragment.dismiss()
                }
            }
            options[1] -> {
                bibleVerse?.let { share(requireContext(), it) }
                delayOnLifecycle(Duration.Delay.DISMISS) {
                    dialogFragment.dismiss()
                }
            }
        }
    }
}