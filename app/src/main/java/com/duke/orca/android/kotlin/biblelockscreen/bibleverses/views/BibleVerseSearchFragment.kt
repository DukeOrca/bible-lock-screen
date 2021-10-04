package com.duke.orca.android.kotlin.biblelockscreen.bibleverses.views

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.ONE_SECOND
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeIn
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeOut
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseChildFragment
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.adapters.BibleVerseAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.copyToClipboard
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.share
import com.duke.orca.android.kotlin.biblelockscreen.bibleverses.viewmodel.BibleVerseSearchViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentBibleVerseSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class BibleVerseSearchFragment : BaseChildFragment<FragmentBibleVerseSearchBinding>(),
    BibleVerseAdapter.OnIconClickListener,
    OptionChoiceDialogFragment.OnOptionChoiceListener {
    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBibleVerseSearchBinding {
        return FragmentBibleVerseSearchBinding.inflate(inflater, container, false)
    }

    private val viewModel by viewModels<BibleVerseSearchViewModel>()
    private val bibleVerseAdapter by lazy { BibleVerseAdapter(activityViewModel.books) }
    private val options by lazy { arrayOf(getString(R.string.copy), getString(R.string.share)) }

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

    override fun onStart() {
        super.onStart()
        activityViewModel.setSystemUiColor()
    }

    override fun onStop() {
        activityViewModel.revertSystemUiColor()
        super.onStop()
    }

    private fun observe() {
        viewModel.results.observe(viewLifecycleOwner, { list ->
            if (list.isNotEmpty()) {
                if (viewBinding.linearLayout.isVisible) {
                    viewBinding.linearLayout.fadeOut(Duration.SHORT)
                }
            } else {
                if (viewBinding.linearLayout.isVisible.not()) {
                    viewBinding.linearLayout.fadeIn(Duration.SHORT)
                }
            }

            viewBinding.recyclerView.scheduleLayoutAnimation()
            bibleVerseAdapter.submitList(list.map { it.toAdapterItem() })
        })
    }

    private fun bind() {
        bibleVerseAdapter.setOnIconClickListener(this)

        val searchMagIcon = viewBinding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        val searchCloseBtn = viewBinding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        val searchSrcText = viewBinding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

        searchMagIcon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        searchCloseBtn?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        searchSrcText?.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.hint_text))
        searchSrcText?.setTextAppearance(R.style.EditTextStyle)
        searchSrcText?.setTextColor(Color.WHITE)

        viewBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                timerTask?.cancel()

                query?.let {
                    if (it.isNotBlank()) {
                        search(it)
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
        viewModel.search(text)
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

    private fun BibleVerse.toAdapterItem() = BibleVerseAdapter.AdapterItem.AdapterBibleVerse(this)
}