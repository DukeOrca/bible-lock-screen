package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.BLANK
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.ONE_SECOND
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeIn
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeOut
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseChildFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.VerseAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.copyToClipboard
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.share
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels.BibleVerseSearchViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentBibleVerseSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class BibleVerseSearchFragment : BaseChildFragment<FragmentBibleVerseSearchBinding>(),
    VerseAdapter.OnIconClickListener,
    OptionChoiceDialogFragment.OnOptionChoiceListener {
    override val toolbar: Toolbar? = null

    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBibleVerseSearchBinding {
        return FragmentBibleVerseSearchBinding.inflate(inflater, container, false)
    }

    private val viewModel by viewModels<BibleVerseSearchViewModel>()
    private val bibleVerseAdapter by lazy { VerseAdapter(viewModel.bibleBook) }
    private val color by lazy { ContextCompat.getColor(requireContext(), R.color.secondary) }
    private val options by lazy { arrayOf(getString(R.string.copy), getString(R.string.share)) }

    private var currentQuery = BLANK
    private var timerTask: TimerTask? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        observe()
        bind()

        return viewBinding.root
    }

    override fun onResume() {
        super.onResume()
        with(viewBinding.searchView) {
            post {
                isIconified = false
            }
        }
    }

    private fun observe() {
        viewModel.searchResult.observe(viewLifecycleOwner, { searchResult ->
            val searchResults = searchResult.searchResults
            val searchWord = searchResult.searchWord

            viewBinding.circularProgressIndicator.fadeOut(Duration.FADE_OUT) {
                bibleVerseAdapter.submitList(
                    searchResults.map { it.toAdapterItem() }, searchWord, color
                ) {
                    delayOnLifecycle(Duration.Delay.DISMISS) {
                        if (searchResults.isEmpty()) {
                            viewBinding.linearLayout.fadeIn(Duration.FADE_IN)
                        } else {
                            viewBinding.recyclerView.fadeIn(Duration.FADE_IN)
                        }
                    }
                }
            }
        })
    }

    private fun bind() {
        bibleVerseAdapter.setOnIconClickListener(this)

        // val searchMagIcon = viewBinding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        // val searchCloseBtn = viewBinding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        val searchSrcText = viewBinding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

        searchSrcText?.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.hint_text))
        searchSrcText?.setTextAppearance(R.style.EditTextStyle)

        viewBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                timerTask?.cancel()

                query?.let {
                    if (it.isNotBlank()) {
                        search(it)
                        viewBinding.searchView.clearFocus()
                    }
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                timerTask?.cancel()

                timerTask = object : TimerTask() {
                    override fun run() {
                        if (newText?.isNotBlank() == true) {
                            if (newText.length > 1) {
                                search(newText)
                            }
                        }
                    }
                }

                Timer().schedule(timerTask, ONE_SECOND)

                return true
            }
        })

        viewBinding.recyclerView.apply {
            adapter = bibleVerseAdapter
            layoutManager = LinearLayoutManagerWrapper(context)
            setHasFixedSize(true)
        }
    }

    private fun search(text: String) {
        if (currentQuery == text) return

        currentQuery = text

        lifecycleScope.launch {
            if (viewBinding.linearLayout.isVisible) {
                viewBinding.linearLayout.fadeOut(Duration.FADE_OUT) {
                    viewBinding.circularProgressIndicator.fadeIn(Duration.FADE_IN)
                    bibleVerseAdapter.submitList(null)
                    viewModel.search(text)
                }
            } else {
                viewBinding.recyclerView.fadeOut(Duration.FADE_OUT, true) {
                    viewBinding.circularProgressIndicator.fadeIn(Duration.FADE_IN)
                    bibleVerseAdapter.submitList(null)
                    viewModel.search(text)
                }
            }
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
                bibleVerse?.let { copyToClipboard(requireContext(), viewModel.bibleBook, it) }
                delayOnLifecycle(Duration.Delay.DISMISS) {
                    dialogFragment.dismiss()
                }
            }
            options[1] -> {
                bibleVerse?.let { share(requireContext(), viewModel.bibleBook, it) }
                delayOnLifecycle(Duration.Delay.DISMISS) {
                    dialogFragment.dismiss()
                }
            }
        }
    }

    private fun BibleVerse.toAdapterItem() = VerseAdapter.AdapterItem.AdapterBibleVerse(this)
}