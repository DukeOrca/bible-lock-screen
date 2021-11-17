package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isInvisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.*
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.BLANK
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseChildFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.BibleChapterPagerAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Translation
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels.BibleChapterPagerViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentBibleChapterPagerBinding
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.Database
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.SubDatabase
import com.duke.orca.android.kotlin.biblelockscreen.settings.views.TranslationSelectionDialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.widget.DropdownMenu
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class BibleChapterPagerFragment : BaseChildFragment<FragmentBibleChapterPagerBinding>(),
    BookSelectionDialogFragment.LifecycleCallback,
    BookSelectionDialogFragment.OnBookSelectedListener,
    BookmarksDialogFragment.OnBookmarkClickListener,
    TranslationSelectionDialogFragment.OnClickListener
{
    override val toolbar: Toolbar by lazy { viewBinding.toolbar }

    private val viewModel by viewModels<BibleChapterPagerViewModel>()

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewModel.get(position)
        }
    }

    private var chapterPagerAdapter: BibleChapterPagerAdapter? = null
    private var currentItem: BibleChapter? = null

    private val names: Array<String>
        get() = Database.getInstance(requireContext()).bibleBookDao().get().names

    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBibleChapterPagerBinding {
        return FragmentBibleChapterPagerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        bind()
    }

    override fun onBookmarkClick(id: Int) {
        viewBinding.viewPager2.setCurrentItem(id, false)
    }

    override fun onBookSelected(
        dialogFragment: BookSelectionDialogFragment,
        item: BookSelectionDialogFragment.AdapterItem.Book
    ) {
        val chapter = 1

        viewBinding.viewPager2.moveTo(item.index.inc(), chapter)

        delayOnLifecycle(Duration.Delay.DISMISS) {
            dialogFragment.dismiss()
        }
    }

    override fun onDialogFragmentViewCreated() {
        delayOnLifecycle(Duration.Delay.ROTATE) {
            viewBinding.imageViewBook.rotate(180.0F, Duration.ROTATION)
        }
    }

    override fun onDialogFragmentViewDestroyed() {
        delayOnLifecycle(Duration.Delay.ROTATE) {
            viewBinding.imageViewBook.rotate(0.0F, Duration.ROTATION)
        }
    }

    override fun onNegativeButtonClick(dialogFragment: DialogFragment) {
        delayOnLifecycle(Duration.Delay.SHORT) {
            dialogFragment.dismiss()
        }
    }

    override fun onPositiveButtonClick(
        dialogFragment: DialogFragment,
        translation: Translation.Model,
        subTranslation: Translation.Model?,
        isTranslationChanged: Boolean,
        isSubTranslationChanged: Boolean
    ) {
        if (isTranslationChanged) {
            DataStore.Translation.putFileName(requireContext(), translation.fileName)
        }

        if (isSubTranslationChanged) {
            subTranslation?.let {
                DataStore.Translation.putSubFileName(requireContext(), it.fileName)
            } ?: run {
                DataStore.Translation.putSubFileName(requireContext(), BLANK)
            }
        }

        if (isTranslationChanged or isSubTranslationChanged) {
            viewBinding.viewPager2.adapter = null
            chapterPagerAdapter = null
        }

        delayOnLifecycle(Duration.Delay.DISMISS) {
            if (isTranslationChanged or isSubTranslationChanged) {
                if (isTranslationChanged) {
                    Database.refresh(requireContext())
                }

                if (isSubTranslationChanged) {
                    SubDatabase.refresh(requireContext())
                }

                onTranslationChanged()
            }

            dialogFragment.dismiss()
        }
    }

    private fun observe() {
        viewModel.currentItem.observe(viewLifecycleOwner, { bibleChapter ->
            bibleChapter?.let {
                viewBinding.textViewBook.text = names[it.book.dec()]
                viewBinding.dropdownMenuChapter.setText(it.chapter.toString())

                if (currentItem?.book.not(it.book)) {
                    viewBinding.dropdownMenuChapter.setAdapter(
                        DropdownMenu.ArrayAdapter(
                            intRange(1, chapters[it.book.dec()]).toStringArray()
                        ), it.chapter.dec()
                    )
                }

                with(viewBinding.linearLayoutBook) {
                    if (isInvisible) {
                        fadeIn(Duration.FADE_IN)
                    }
                }

                with(viewBinding.dropdownMenuChapter) {
                    if (isInvisible) {
                        fadeIn(Duration.FADE_IN)
                    }
                }

                currentItem = it
                DataStore.BibleChapter.putCurrentChapter(requireContext(), it.id)
            }
        })
    }

    private fun bind() {
        viewBinding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        viewBinding.linearLayoutBook.setOnClickListener {
            viewBinding.imageViewBook.performClick()
        }

        viewBinding.imageViewBook.setOnClickListener {
            BookSelectionDialogFragment.newInstance(
                currentItem?.book ?: 0
            ).also {
                it.show(childFragmentManager, it.tag)
            }
        }

        viewBinding.dropdownMenuChapter.setOnItemClickListener { position, _ ->
            val book = currentItem?.book ?: 1

            viewBinding.viewPager2.moveTo(book, position.inc())
        }

        lifecycleScope.launch {
            val bookChapters = viewModel.getAll()

            chapterPagerAdapter = BibleChapterPagerAdapter(
                this@BibleChapterPagerFragment,
                bookChapters
            )

            viewBinding.viewPager2.apply {
                adapter = chapterPagerAdapter
                registerOnPageChangeCallback(onPageChangeCallback)
                setCurrentItem(
                    DataStore.BibleChapter.getCurrentChapter(requireContext()),
                    false
                )
            }
        }

        viewBinding.imageViewBookmarks.setOnClickListener {
            BookmarksDialogFragment().also {
                it.show(childFragmentManager, it.tag)
            }
        }

        viewBinding.imageViewCrayon.setOnClickListener {

        }

        viewBinding.imageViewFavorites.setOnClickListener {
            addFragment(FavoritesFragment())
        }

        viewBinding.imageViewTypography.setOnClickListener {

        }

        viewBinding.imageViewDarkMode.setOnClickListener {

        }

        viewBinding.imageViewTranslate.setOnClickListener {
            TranslationSelectionDialogFragment().also {
                it.show(childFragmentManager, it.tag)
            }
        }
    }

    private fun addFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .setReorderingAllowed(true)
            .add(R.id.fragment_container_view, fragment, fragment.tag)
            .addToBackStack(fragment.tag)
            .commit()
    }

    private fun onTranslationChanged() {
        lifecycleScope.launch {
            viewModel.refresh()

            val bookChapters = viewModel.getAll()

            chapterPagerAdapter = BibleChapterPagerAdapter(
                this@BibleChapterPagerFragment,
                bookChapters
            )

            viewBinding.viewPager2.apply {
                adapter = chapterPagerAdapter
                registerOnPageChangeCallback(onPageChangeCallback)
                setCurrentItem(
                    DataStore.BibleChapter.getCurrentChapter(requireContext()),
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