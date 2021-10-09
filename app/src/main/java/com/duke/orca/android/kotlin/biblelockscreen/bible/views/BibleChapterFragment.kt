package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.Application
import com.duke.orca.android.kotlin.biblelockscreen.application.setTint
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseViewStubFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.BibleVerseAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BookChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodel.BibleChapterViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentBibleChapterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BibleChapterFragment : BaseViewStubFragment() {
    override val layoutResource: Int
        get() = R.layout.fragment_bible_chapter

    private val viewModel by viewModels<BibleChapterViewModel>()
    private val bookChapter by lazy { arguments?.getParcelable<BookChapter>(Key.BOOK_CHAPTER) }
    private val book by lazy { bookChapter?.book ?: 1 }
    private val chapter by lazy { bookChapter?.chapter ?: 1 }
    private val bibleVerseAdapter by lazy { BibleVerseAdapter(activityViewModel.books) }

    private var bibleChapter: BibleChapter? = null
    private var binding: FragmentBibleChapterBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        initData()

        return viewBinding.root
    }

    private fun initData() {
        viewModel.getBibleChapter(book, chapter)
        viewModel.getBibleVerses(book, chapter)
    }

    private fun observe() {
        lifecycleScope.launchWhenResumed {
            viewModel.adapterItems.observe(viewLifecycleOwner, {
                bibleVerseAdapter.submitList(it)
            })

            viewModel.bibleChapter.observe(viewLifecycleOwner, {
                bibleChapter = it

                binding?.imageViewBookmark?.setTint(
                    if (it.bookmark) {
                        R.color.bookmarked
                    } else {
                        R.color.unbookmarked
                    }
                )
            })
        }
    }

    override fun onInflated(view: View) {
        with(FragmentBibleChapterBinding.bind(view)){
            observe()
            binding = this

            recyclerViewBibleChapter.apply {
                adapter = bibleVerseAdapter
                layoutManager = LinearLayoutManagerWrapper(requireContext())
                scheduleLayoutAnimation()
                setHasFixedSize(true)
            }

            imageViewBookmark.setOnClickListener {
                bibleChapter?.let { bibleChapter ->
                    viewModel.updateBookmark(bibleChapter.id, bibleChapter.bookmark.not())
                }
            }
        }

        viewModel.getBibleChapter(book, chapter)
    }

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.bible.views"

        private object Key {
            const val BOOK_CHAPTER = "$PACKAGE_NAME.BOOK_CHAPTER"
        }

        fun newInstance(bookChapter: BookChapter): BibleChapterFragment {
            return BibleChapterFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Key.BOOK_CHAPTER, bookChapter)
                }
            }
        }
    }
}