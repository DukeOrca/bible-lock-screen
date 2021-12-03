package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isInvisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.EXTRA_RECREATE
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.BLANK
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeIn
import com.duke.orca.android.kotlin.biblelockscreen.application.rotate
import com.duke.orca.android.kotlin.biblelockscreen.application.toStringArray
import com.duke.orca.android.kotlin.biblelockscreen.base.viewmodels.FragmentContainerViewModel
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseChildFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.ChapterPagerAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.*
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels.ChapterPagerViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentChapterPagerBinding
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.Database
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.SubDatabase
import com.duke.orca.android.kotlin.biblelockscreen.settings.views.FontSettingsBottomSheetDialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.settings.views.TranslationSelectionDialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.widget.DropdownMenu
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

//@AndroidEntryPoint
//class ChapterPagerFragment : BaseChildFragment<FragmentChapterPagerBinding>(),
//    BookSelectionDialogFragment.LifecycleCallback,
//    BookSelectionDialogFragment.OnBookSelectedListener,
//    BookmarksDialogFragment.OnBookmarkClickListener,
//    TranslationSelectionDialogFragment.OnClickListener
//{
//    override val toolbar: Toolbar by lazy { viewBinding.toolbar }
//
//    private val activityViewModel by activityViewModels<FragmentContainerViewModel>()
//    private val viewModel by viewModels<ChapterPagerViewModel>()
//
//    private val fragment: ChapterPagerFragment
//        get() = this
//
//    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
//        override fun onPageSelected(position: Int) {
//            super.onPageSelected(position)
//            viewModel.setCurrentChapter(position.inc())
//        }
//    }
//
//    private var chapterPagerAdapter: ChapterPagerAdapter? = null
//    private var currentBook = 1
//    private var currentChapter = 1
//
//    private val names: Array<String>
//        get() = Database.getInstance(requireContext()).bibleBookDao().get().names
//
//    override fun inflate(
//        inflater: LayoutInflater,
//        container: ViewGroup?
//    ): FragmentChapterPagerBinding {
//        return FragmentChapterPagerBinding.inflate(inflater, container, false)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        super.onCreateView(inflater, container, savedInstanceState)
//        setup()
//
//        return viewBinding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        bind()
//        observe()
//    }
//
//    override fun onBookmarkClick(id: Int) {
//        viewBinding.viewPager2.setCurrentItem(id, false)
//    }
//
//    override fun onBookSelected(
//        dialogFragment: BookSelectionDialogFragment,
//        item: BookSelectionDialogFragment.AdapterItem.Book
//    ) {
//        delayOnLifecycle(Duration.Delay.DISMISS) {
//            viewModel.setCurrentBook(item.book)
//            dialogFragment.dismiss()
//        }
//    }
//
//    override fun onDialogFragmentViewCreated() {
//        delayOnLifecycle(Duration.Delay.ROTATE) {
//            viewBinding.imageViewBook.rotate(180.0f, Duration.ROTATION)
//        }
//    }
//
//    override fun onDialogFragmentViewDestroyed() {
//        delayOnLifecycle(Duration.Delay.ROTATE) {
//            viewBinding.imageViewBook.rotate(0.0f, Duration.ROTATION)
//        }
//    }
//
//    override fun onNegativeButtonClick(dialogFragment: DialogFragment) {
//        delayOnLifecycle(Duration.Delay.SHORT) {
//            dialogFragment.dismiss()
//        }
//    }
//
//    override fun onPositiveButtonClick(
//        dialogFragment: DialogFragment,
//        translation: Translation.Model,
//        subTranslation: Translation.Model?,
//        isTranslationChanged: Boolean,
//        isSubTranslationChanged: Boolean
//    ) {
//        if (isTranslationChanged) {
//            DataStore.Translation.putFileName(requireContext(), translation.fileName)
//        }
//
//        if (isSubTranslationChanged) {
//            subTranslation?.let {
//                DataStore.Translation.putSubFileName(requireContext(), it.fileName)
//            } ?: run {
//                DataStore.Translation.putSubFileName(requireContext(), BLANK)
//            }
//        }
//
//        if (isTranslationChanged or isSubTranslationChanged) {
//            viewBinding.viewPager2.adapter = null
//            chapterPagerAdapter = null
//
//            activityViewModel.setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_RECREATE, true))
//        }
//
//        delayOnLifecycle(Duration.Delay.DISMISS) {
//            if (isTranslationChanged or isSubTranslationChanged) {
//                if (isTranslationChanged) {
//                    Database.refresh(requireContext())
//                }
//
//                if (isSubTranslationChanged) {
//                    SubDatabase.refresh(requireContext())
//                }
//
//                onTranslationChanged()
//            }
//
//            dialogFragment.dismiss()
//        }
//    }
//
//    private fun setup() {
//        arguments?.getParcelable<Verse>(Key.VERSE)?.let {
//            viewModel.setCurrentBook(it.book)
//            viewModel.setCurrentChapter(it.chapter)
//        } ?: viewModel.initRecentlyRead()
//    }
//
//    private fun observe() {
//        viewModel.currentBook.observe(viewLifecycleOwner, { book ->
//            with(viewBinding) {
//                textViewBook.text = names[book.dec()]
//
//                val chapters =  BookToChapters.get(book).toStringArray()
//
//                dropdownMenuChapter.setAdapter(DropdownMenu.ArrayAdapter(chapters))
//
//                chapterPagerAdapter = ChapterPagerAdapter(fragment, book)
//
//                viewBinding.viewPager2.apply {
//                    adapter = chapterPagerAdapter
//                    registerOnPageChangeCallback(onPageChangeCallback)
//                }
//            }
//
//            currentBook = book
//        })
//
//        viewModel.currentChapter.observe(viewLifecycleOwner, { chapter ->
//            with(viewBinding) {
//                dropdownMenuChapter.setText("$chapter")
//
//                with(linearLayoutBook) {
//                    if (isInvisible) {
//                        fadeIn(Duration.FADE_IN)
//                    }
//                }
//
//                with(dropdownMenuChapter) {
//                    if (isInvisible) {
//                        fadeIn(Duration.FADE_IN)
//                    }
//                }
//            }
//
//            DataStore.Chapter.putCurrentChapter(requireContext(), chapter)
//
//            currentChapter = chapter
//        })
//    }
//
//    private fun bind() {
//        with(viewBinding) {
//            toolbar.setNavigationOnClickListener {
//                requireActivity().onBackPressed()
//            }
//
//            linearLayoutBook.setOnClickListener {
//                viewBinding.imageViewBook.performClick()
//            }
//
//            imageViewBook.setOnClickListener {
//                BookSelectionDialogFragment.newInstance(
//                    currentBook
//                ).also {
//                    it.show(childFragmentManager, it.tag)
//                }
//            }
//
//            dropdownMenuChapter.setOnItemClickListener { position, _ ->
//                val smoothScroll = abs(viewPager2.currentItem - position) < 3
//
//                viewPager2.setCurrentItem(position, smoothScroll)
//            }
//        }
//
//        viewBinding.imageViewHighlight.setOnClickListener {
//
//        }
//
//        viewBinding.imageViewBookmarks.setOnClickListener {
//            BookmarksDialogFragment().also {
//                it.show(childFragmentManager, it.tag)
//            }
//        }
//
//        viewBinding.imageViewFavorites.setOnClickListener {
//            addFragment(FavoritesFragment())
//        }
//
//        viewBinding.imageViewFont.setOnClickListener {
//            FontSettingsBottomSheetDialogFragment().also {
//                it.show(childFragmentManager, it.tag)
//            }
//        }
//
//        val isDarkMode = DataStore.Display.isDarkMode(requireContext())
//
//        if (isDarkMode) {
//            viewBinding.imageViewDarkMode.setImageResource(R.drawable.ic_round_light_mode_24)
//        } else {
//            viewBinding.imageViewDarkMode.setImageResource(R.drawable.ic_round_dark_mode_24)
//        }
//
//        viewBinding.imageViewDarkMode.setOnClickListener {
//            DataStore.Display.putDarkMode(it.context, isDarkMode.not())
//            delayOnLifecycle(Duration.Delay.RECREATE) {
//                activity?.recreate()
//            }
//        }
//
//        viewBinding.imageViewTranslate.setOnClickListener {
//            TranslationSelectionDialogFragment().also {
//                it.show(childFragmentManager, it.tag)
//            }
//        }
//    }
//
//    private fun addFragment(fragment: Fragment) {
//        parentFragmentManager.beginTransaction()
//            .setCustomAnimations(
//                R.anim.slide_in_right,
//                R.anim.slide_out_left,
//                R.anim.slide_in_left,
//                R.anim.slide_out_right
//            )
//            .setReorderingAllowed(true)
//            .add(R.id.fragment_container_view, fragment, fragment.tag)
//            .addToBackStack(fragment.tag)
//            .commit()
//    }
//
//    private fun onTranslationChanged() {
//        lifecycleScope.launch {
//            setup()
//        }
//    }
//
//    companion object {
//        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.bible.views"
//
//        private object Key {
//            private const val OBJECT_NAME = "Key"
//            const val VERSE = "$PACKAGE_NAME.$OBJECT_NAME.VERSE"
//        }
//
//        fun newInstance(verse: Verse): ChapterPagerFragment {
//            return ChapterPagerFragment().apply {
//                arguments = Bundle().apply {
//                    putParcelable(Key.VERSE, verse)
//                }
//            }
//        }
//    }
//}