package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeIn
import com.duke.orca.android.kotlin.biblelockscreen.application.setIntegerArrayAdapter
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseViewStubFragment
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
class BibleChapterPagerFragment : BaseViewStubFragment(),
    BookmarksDialogFragment.OnBookmarkClickListener {
    override val layoutResource: Int
        get() = R.layout.fragment_bible_chapter_pager
    override val showCircularProgressIndicator: Boolean
        get() = true

    private val viewModel by viewModels<BibleChapterPagerViewModel>()

    private var binding: FragmentBibleChapterPagerBinding? = null
    private var currentItem: BibleChapter? = null

    private val bookAdapter by lazy {
        ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            activityViewModel.books
        )
    }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewModel.get(position)
        }
    }

    private var bibleChapterPagerAdapter: BibleChapterPagerAdapter? = null

    override fun onInflated(view: View) {
        binding = FragmentBibleChapterPagerBinding.bind(view)
        binding?.let {
            observe(it)
            bind(it)
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return AnimationUtils.loadAnimation(requireContext(), nextAnim).apply {
            setAnimationListener( object: Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (enter) {
                        activityViewModel.callCloseDrawer()
                    }
                }

                override fun onAnimationStart(animation: Animation?) {
                    if (enter) {
                        activityViewModel.setSystemUiColor()
                    } else {
                        activityViewModel.revertSystemUiColor()
                    }
                }
            })
        }
    }

    override fun onPause() {
        DataStore.BibleChapter.putCurrentItem(requireContext(), currentItem?.id ?: 0)
        super.onPause()
    }

    override fun onBookmarkClick(id: Int) {
        binding?.viewPager2?.setCurrentItem(id, false)
    }

    private fun observe(binding: FragmentBibleChapterPagerBinding) {
        viewModel.currentItem.observe(viewLifecycleOwner, { bibleChapter ->
            bibleChapter?.let {
                binding.exposedDropdownMenuBook.autoCompleteTextView.setText(
                    activityViewModel.getBook(it.book),
                    false
                )
                binding.exposedDropdownMenuChapter.autoCompleteTextView.setText(
                    it.chapter.toString(),
                    false
                )

                binding.exposedDropdownMenuBook.autoCompleteTextView.setAdapter(bookAdapter)

                if (currentItem?.book != it.book) {
                    binding.exposedDropdownMenuChapter.autoCompleteTextView.setIntegerArrayAdapter(
                        activityViewModel.chapters[it.book.dec()], R.layout.dropdown_item
                    )
                }

                with(binding.constraintLayout) {
                    if (isInvisible) {
                        delayOnLifecycle(Duration.SHORT) {
                            fadeIn(Duration.SHORT)
                        }
                    }
                }

                currentItem = it
            }
        })
    }

    private fun bind(binding: FragmentBibleChapterPagerBinding) {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.imageViewBookmarks.setOnClickListener {
            BookmarksDialogFragment().also {
                it.show(childFragmentManager, it.tag)
            }
        }

        binding.exposedDropdownMenuBook.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val chapter = 1

            binding.viewPager2.moveTo(position.inc(), chapter)
        }

        binding.exposedDropdownMenuChapter.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val book = currentItem?.book ?: 1

            binding.viewPager2.moveTo(book, position.inc())
        }

        with(binding.viewPager2) {
            delayOnLifecycle(Duration.SHORT) {
                fadeIn(Duration.SHORT) {
                    setAdapter(this)
                }
            }
        }
    }

    private fun setAdapter(viewPager2: ViewPager2) {
        lifecycleScope.launch {
            val bookChapters = viewModel.getAll()

            bibleChapterPagerAdapter = BibleChapterPagerAdapter(
                this@BibleChapterPagerFragment,
                bookChapters
            )

            viewPager2.adapter = null
            viewPager2.adapter = bibleChapterPagerAdapter
            viewPager2.offscreenPageLimit = 2
            viewPager2.registerOnPageChangeCallback(onPageChangeCallback)
            viewPager2.setCurrentItem(
                DataStore.BibleChapter.getCurrentItem(requireContext()),
                false
            )
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