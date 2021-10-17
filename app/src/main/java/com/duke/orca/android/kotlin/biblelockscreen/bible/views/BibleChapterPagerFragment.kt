package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeIn
import com.duke.orca.android.kotlin.biblelockscreen.application.setIntegerArrayAdapter
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseChildFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.BibleChapterPagerAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodel.BibleChapterPagerViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentBibleChapterPagerBinding
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class BibleChapterPagerFragment : BaseChildFragment<FragmentBibleChapterPagerBinding>(),
    BookmarksDialogFragment.OnBookmarkClickListener {
    override val toolbar: Toolbar by lazy { viewBinding.toolbar }

    private val viewModel by viewModels<BibleChapterPagerViewModel>()

    private val bookAdapter by lazy {
        ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            books
        )
    }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewModel.get(position)
        }
    }

    private var bibleChapterPagerAdapter: BibleChapterPagerAdapter? = null
    private var currentItem: BibleChapter? = null

    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBibleChapterPagerBinding {
        return FragmentBibleChapterPagerBinding.inflate(inflater, container, false)
    }

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

    override fun onPause() {
        DataStore.BibleChapter.putCurrentItem(requireContext(), currentItem?.id ?: 0)
        super.onPause()
    }

    override fun onBookmarkClick(id: Int) {
        viewBinding.viewPager2.setCurrentItem(id, false)
    }

    private fun observe() {
        viewModel.currentItem.observe(viewLifecycleOwner, { bibleChapter ->
            bibleChapter?.let {
                viewBinding.exposedDropdownMenuBook.autoCompleteTextView.setText(
                    getBook(it.book),
                    false
                )
                viewBinding.exposedDropdownMenuChapter.autoCompleteTextView.setText(
                    it.chapter.toString(),
                    false
                )

                viewBinding.exposedDropdownMenuBook.autoCompleteTextView.setAdapter(bookAdapter)

                if (currentItem?.book != it.book) {
                    viewBinding.exposedDropdownMenuChapter.autoCompleteTextView.setIntegerArrayAdapter(
                        chapters[it.book.dec()], R.layout.dropdown_item
                    )
                }

                with(viewBinding.constraintLayout) {
                    if (isInvisible) {
                        delayOnLifecycle(Duration.SHORT) {
                            fadeIn(Duration.MEDIUM)
                        }
                    }
                }

                currentItem = it
            }
        })
    }

    private fun bind() {
        viewBinding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        viewBinding.imageViewBookmarks.setOnClickListener {
            BookmarksDialogFragment().also {
                it.show(childFragmentManager, it.tag)
            }
        }

        viewBinding.exposedDropdownMenuBook.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val chapter = 1

            viewBinding.viewPager2.moveTo(position.inc(), chapter)
        }

        viewBinding.exposedDropdownMenuChapter.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val book = currentItem?.book ?: 1

            viewBinding.viewPager2.moveTo(book, position.inc())
        }

        lifecycleScope.launch {
            val bookChapters = viewModel.getAll()

            bibleChapterPagerAdapter = BibleChapterPagerAdapter(
                this@BibleChapterPagerFragment,
                bookChapters
            )

            viewBinding.viewPager2.apply {
                adapter = bibleChapterPagerAdapter
                offscreenPageLimit = 2
                registerOnPageChangeCallback(onPageChangeCallback)
                setCurrentItem(
                    DataStore.BibleChapter.getCurrentItem(requireContext()),
                    false
                )
            }
        }
    }

    private fun ViewPager2.moveTo(book: Int, chapter: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val item = viewModel.get(book, chapter).id

            withContext(Dispatchers.Main) {
                setCurrentItem(item, false)
            }
        }
    }
}