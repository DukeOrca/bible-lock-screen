package com.duke.orca.android.kotlin.biblelockscreen.bibleverses.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.Duration
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseChildFragment
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.adapters.BibleVerseAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.copyToClipboard
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.share
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.viewmodel.FavoritesViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentFavoritesBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class FavoritesFragment : BaseChildFragment<FragmentFavoritesBinding>(),
    BibleVerseAdapter.OnIconClickListener,
    OptionChoiceDialogFragment.OnOptionChoiceListener {
    override val changeSystemUiColor: Boolean = true
    override val onAnimationEnd: ((enter: Boolean) -> Unit)
        get() = { enter ->
            if (enter) {
                observe()
            }
        }

    override val toolbar: Toolbar by lazy { viewBinding.toolbar }

    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoritesBinding {
        return FragmentFavoritesBinding.inflate(inflater, container, false)
    }

    private val viewModel by viewModels<FavoritesViewModel>()
    private val bibleVerseAdapter by lazy { BibleVerseAdapter(activityViewModel.books) }
    private val options by lazy { arrayOf(getString(R.string.copy), getString(R.string.share)) }
    private val isLayoutAnimationScheduled = AtomicBoolean(false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        viewModel.getFavorites()
        bind()

        return viewBinding.root
    }

    private fun observe() {
        viewModel.adapterItems.observe(viewLifecycleOwner, {
            if (isLayoutAnimationScheduled.compareAndSet(false, true)) {
                viewBinding.recyclerView.scheduleLayoutAnimation()
            }

            bibleVerseAdapter.submitList(it)
        })
    }

    private fun bind() {
        bibleVerseAdapter.setOnIconClickListener(this)

        viewBinding.recyclerView.apply {
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
                delayOnLifecycle(Duration.SHORT) {
                    dialogFragment.dismiss()
                }
            }
            options[1] -> {
                bibleVerse?.let { share(requireContext(), it) }
                delayOnLifecycle(Duration.SHORT) {
                    dialogFragment.dismiss()
                }
            }
        }
    }
}