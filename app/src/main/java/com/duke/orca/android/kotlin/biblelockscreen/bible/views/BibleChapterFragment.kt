package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.SimpleItemAnimator
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeIn
import com.duke.orca.android.kotlin.biblelockscreen.application.setTint
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseViewStubFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.BibleVerseAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.copyToClipboard
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BookChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.share
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodel.BibleChapterViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentBibleChapterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BibleChapterFragment : BaseViewStubFragment(),
    BibleVerseAdapter.OnIconClickListener,
    OptionChoiceDialogFragment.OnOptionChoiceListener
{
    override val layoutResource: Int
        get() = R.layout.fragment_bible_chapter

    private val viewModel by viewModels<BibleChapterViewModel>()
    private val bookChapter by lazy { arguments?.getParcelable<BookChapter>(Key.BOOK_CHAPTER) }
    private val book by lazy { bookChapter?.book ?: 1 }
    private val chapter by lazy { bookChapter?.chapter ?: 1 }
    private val bibleVerseAdapter by lazy { BibleVerseAdapter(books) }
    private val options by lazy { arrayOf(getString(R.string.copy), getString(R.string.share)) }

    private var bibleChapter: BibleChapter? = null

    private var _binding: FragmentBibleChapterBinding? = null
    private val binding: FragmentBibleChapterBinding get() = _binding!!

    override fun onInflated(view: View) {
        bibleVerseAdapter.setOnIconClickListener(this)

        _binding = FragmentBibleChapterBinding.bind(view)

        viewModel.bibleChapter.observe(viewLifecycleOwner, {
            bibleChapter = it

            binding.imageViewBookmark.setTint(
                if (it.bookmark) {
                    R.color.bookmarked
                } else {
                    R.color.unbookmarked
                }
            )
        })

        binding.linearLayout.fadeIn(Duration.MEDIUM)

        binding.recyclerViewBibleChapter.apply {
            adapter = bibleVerseAdapter
            layoutManager = LinearLayoutManagerWrapper(requireContext())
            scheduleLayoutAnimation()
            setHasFixedSize(true)

            with(itemAnimator) {
                if (this is SimpleItemAnimator) {
                    supportsChangeAnimations = false
                }
            }
        }

        binding.imageViewBookmark.setOnClickListener {
            bibleChapter?.let { bibleChapter ->
                viewModel.updateBookmark(bibleChapter.id, bibleChapter.bookmark.not())
            }
        }

        viewModel.getBibleChapter(book, chapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        initData()
        observe()

        return viewBinding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initData() {
        viewModel.getBibleChapter(book, chapter)
        viewModel.getBibleVerses(book, chapter)
    }

    private fun observe() {
        viewModel.adapterItems.observe(viewLifecycleOwner, {
            bibleVerseAdapter.submitList(it)
        })
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