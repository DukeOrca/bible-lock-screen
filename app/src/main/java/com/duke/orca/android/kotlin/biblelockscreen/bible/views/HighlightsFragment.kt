package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.`is`
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Key
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.RequestKey
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.HighlightAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.VerseAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels.HighlightsViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentHighlightsBinding
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class HighlightsFragment : BaseFragment<FragmentHighlightsBinding>() {
    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHighlightsBinding {
        return FragmentHighlightsBinding.inflate(inflater, container, false)
    }

    private val viewModel by viewModels<HighlightsViewModel>()
    private val fragmentResultSetRequired by lazy {
        arguments?.getBoolean(Key.FRAGMENT_RESULT_SET_REQUIRED) ?: false
    }

    private val highlightAdapter by lazy {
        HighlightAdapter { highlight ->
            val adapterVerses = highlight.verses.map { VerseAdapter.AdapterItem.AdapterVerse(it) }
            val map = adapterVerses.groupBy { VerseAdapter.AdapterItem.AdapterBook(it.verse.book) }

            verseAdapter.submitMap(map)
        }
    }

    private val verseAdapter by lazy {
        VerseAdapter(bible) {
            if (fragmentResultSetRequired) {
                viewModel.insertPosition(it.toPosition())

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

    private var selectedItem = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind()
        observe()
    }

    private fun bind() {
        with(viewBinding) {
            recyclerViewHighlight.apply {
                adapter = highlightAdapter
                layoutManager = LinearLayoutManagerWrapper(context, LinearLayoutManager.HORIZONTAL)
                setHasFixedSize(true)
            }

            recyclerViewVerse.apply {
                adapter = verseAdapter
                layoutManager = LinearLayoutManagerWrapper(context)
                setHasFixedSize(true)
            }
        }
    }

    private fun observe() {
        compositeDisposable.add(viewModel.highlights
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { list ->
                val map = list.groupBy { it.highlightColor }

                highlightAdapter.submitMap(map) {
                    if (selectedItem.`is`(-1)) {
                        highlightAdapter.select(0)
                    }
                }
            }
        )
    }

    companion object {
        fun newInstance(fragmentResultSetRequired: Boolean): HighlightsFragment {
            return HighlightsFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(Key.FRAGMENT_RESULT_SET_REQUIRED, fragmentResultSetRequired)
                }
            }
        }
    }
}