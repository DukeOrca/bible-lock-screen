package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class BibleChapterPagerFragment : BaseViewStubFragment() {
    override val layoutResource: Int
        get() = R.layout.fragment_bible_chapter_pager

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

    private fun observe(binding: FragmentBibleChapterPagerBinding) {
        lifecycleScope.launchWhenResumed {
            bibleChapterPagerAdapter = BibleChapterPagerAdapter(
                this@BibleChapterPagerFragment,
                viewModel.getAll()
            )

            binding.viewPager2.adapter = null
            binding.viewPager2.adapter = bibleChapterPagerAdapter
            binding.viewPager2.offscreenPageLimit = 2
            binding.viewPager2.registerOnPageChangeCallback(onPageChangeCallback)

            viewModel.currentItem.observe(viewLifecycleOwner, { bibleVerse ->
                bibleVerse?.let {
                    binding.exposedDropdownMenuBook.autoCompleteTextView.setText(
                        activityViewModel.getBook(it.book),
                        false
                    )
                    binding.exposedDropdownMenuChapter.autoCompleteTextView.setText(
                        it.chapter.toString(),
                        false
                    )

                    if (currentItem?.book != it.book) {
                        binding.exposedDropdownMenuChapter.autoCompleteTextView.setIntegerArrayAdapter(
                            activityViewModel.chapters[it.book.dec()], R.layout.dropdown_item
                        )
                    }

                    currentItem = it
                }
            })
        }
    }

    private fun bind(binding: FragmentBibleChapterPagerBinding) {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.exposedDropdownMenuBook.autoCompleteTextView.setAdapter(bookAdapter)
        binding.exposedDropdownMenuBook.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val chapter = 1

            binding.viewPager2.moveTo(position.inc(), chapter)
        }

        binding.exposedDropdownMenuChapter.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val book = currentItem?.book ?: 1

            binding.viewPager2.moveTo(book, position.inc())
        }

        binding.constraintLayout.fadeIn(Duration.LONG)
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