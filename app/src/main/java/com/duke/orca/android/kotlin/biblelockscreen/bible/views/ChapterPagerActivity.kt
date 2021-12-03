package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isInvisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.*
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.BLANK
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseLockScreenActivity
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.ChapterPagerAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Book
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BookToChapters
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Translation
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Verse
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels.ChapterPagerViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.ActivityChapterPagerBinding
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.datastore.RecentlyRead
import com.duke.orca.android.kotlin.biblelockscreen.datastore.recentlyReadDataStore
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.Database
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.SubDatabase
import com.duke.orca.android.kotlin.biblelockscreen.settings.views.*
import com.duke.orca.android.kotlin.biblelockscreen.widget.DropdownMenu
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class ChapterPagerActivity : BaseLockScreenActivity(),
    BookSelectionDialogFragment.LifecycleCallback,
    BookSelectionDialogFragment.OnBookSelectedListener,
    BookmarksDialogFragment.OnBookmarkClickListener,
    TranslationSelectionDialogFragment.OnClickListener
{
    private var _viewBinding: ActivityChapterPagerBinding? = null
    private val viewBinding: ActivityChapterPagerBinding
        get() = _viewBinding!!

    private val viewModel by viewModels<ChapterPagerViewModel>()

    private val book: Book
        get() = Database.getInstance(this).bibleBookDao().get()

    private var chapterPagerAdapter: ChapterPagerAdapter? = null
    private var currentBook = 1
    private var currentChapter = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            if (it.getBoolean(Key.RECREATE)) {
                setDarkMode()
            }
        }

        super.onCreate(savedInstanceState)
        _viewBinding = ActivityChapterPagerBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        bind()
        observe()

        intent?.let {
            if (it.hasExtra(EXTRA_RECENTLY_READ)) {
                val byteArray = it.getByteArrayExtra(EXTRA_RECENTLY_READ)
                val recentlyRead =  RecentlyRead.parseFrom(byteArray)

                setup(recentlyRead.book, recentlyRead.chapter)
            } else if (it.hasExtra(EXTRA_VERSE)) {
                val verse = it.getParcelableExtra<Verse>(EXTRA_VERSE) ?: return@let

                setup(verse.book, verse.chapter)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(Key.RECREATE, true)
    }

    override fun onPause() {
        viewModel.updateRecentlyRead(currentBook, currentChapter) // dy 는 vm 에서 넘겨받아야함
        viewModel.updateDy()
        super.onPause()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.z_adjustment_bottom, R.anim.slide_out_right)
    }

    override fun onBookmarkClick(id: Int) {
        viewBinding.viewPager2.setCurrentItem(id, false)
    }

    override fun onBookSelected(
        dialogFragment: BookSelectionDialogFragment,
        item: BookSelectionDialogFragment.AdapterItem.Book
    ) {
        delayOnLifecycle(Duration.Delay.DISMISS) {
            setup(item.book, 1)
            dialogFragment.dismiss()
        }
    }

    override fun onDialogFragmentViewCreated() {
        delayOnLifecycle(Duration.Delay.ROTATE) {
            viewBinding.imageViewBook.rotate(180.0f, Duration.ROTATION)
        }
    }

    override fun onDialogFragmentViewDestroyed() {
        delayOnLifecycle(Duration.Delay.ROTATE) {
            viewBinding.imageViewBook.rotate(0.0f, Duration.ROTATION)
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
            DataStore.Translation.putFileName(this, translation.fileName)
        }

        if (isSubTranslationChanged) {
            subTranslation?.let {
                DataStore.Translation.putSubFileName(this, it.fileName)
            } ?: run {
                DataStore.Translation.putSubFileName(this, BLANK)
            }
        }

        if (isTranslationChanged or isSubTranslationChanged) {
            viewBinding.viewPager2.adapter = null
            chapterPagerAdapter = null

            setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_RECREATE, true))
        }

        delayOnLifecycle(Duration.Delay.DISMISS) {
            if (isTranslationChanged or isSubTranslationChanged) {
                if (isTranslationChanged) {
                    Database.refresh(this)
                }

                if (isSubTranslationChanged) {
                    SubDatabase.refresh(this)
                }

                onTranslationChanged()
            }

            dialogFragment.dismiss()
        }
    }

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewModel.setCurrentChapter(position.inc())
        }
    }

    private fun setDarkMode() {
        if (DataStore.Display.isDarkMode(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun bind() {
        with(viewBinding) {
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }

            linearLayoutBook.setOnClickListener {
                viewBinding.imageViewBook.performClick()
            }

            imageViewBook.setOnClickListener {
                BookSelectionDialogFragment.newInstance(
                    currentBook
                ).also {
                    it.show(supportFragmentManager, it.tag)
                }
            }

            dropdownMenuChapter.setOnItemClickListener { position, _ ->
                val smoothScroll = abs(viewPager2.currentItem - position) < 3

                viewPager2.setCurrentItem(position, smoothScroll)
            }

            imageViewHighlight.setOnClickListener {

            }

            imageViewBookmarks.setOnClickListener {
                BookmarksDialogFragment().also {
                    it.show(supportFragmentManager, it.tag)
                }
            }

            imageViewFavorites.setOnClickListener {
                addFragment(FavoritesFragment())
            }

            imageViewFont.setOnClickListener {
                FontSettingsBottomSheetDialogFragment().also {
                    it.show(supportFragmentManager, it.tag)
                }
            }

            val isDarkMode = DataStore.Display.isDarkMode(activity)

            if (isDarkMode) {
                imageViewDarkMode.setImageResource(R.drawable.ic_round_light_mode_24)
            } else {
                imageViewDarkMode.setImageResource(R.drawable.ic_round_dark_mode_24)
            }

            imageViewDarkMode.setOnClickListener {
                DataStore.Display.putDarkMode(it.context, isDarkMode.not())

                viewModel.updateRecentlyRead(currentBook, currentChapter)

                lifecycleScope.launch(Dispatchers.Main) {
                    val recentlyRead = recentlyReadDataStore.data.flowOn(Dispatchers.IO).first()

                    intent?.putExtra(EXTRA_RECENTLY_READ, recentlyRead.toByteArray())

                    delayOnLifecycle(Duration.Delay.RECREATE) {
                        recreate()
                    }
                }
            }

            imageViewTranslate.setOnClickListener {
                TranslationSelectionDialogFragment().also {
                    it.show(supportFragmentManager, it.tag)
                }
            }
        }
    }

    private fun setup(book: Int, chapter: Int) {
        with(viewBinding) {
            textViewBook.text = this@ChapterPagerActivity.book.name(book)

            val chapters =  BookToChapters.get(book).toStringArray()

            dropdownMenuChapter.setAdapter(DropdownMenu.ArrayAdapter(chapters))

            chapterPagerAdapter = ChapterPagerAdapter(activity, book)

            viewPager2.apply {
                adapter = chapterPagerAdapter
                registerOnPageChangeCallback(onPageChangeCallback)
                setCurrentItem(chapter.dec(), false)
            }
        }

        currentBook = book
    }

    private fun observe() {
        viewModel.currentChapter.observe(this, {
            with(viewBinding) {
                dropdownMenuChapter.setText("$it")

                with(linearLayoutBook) {
                    if (isInvisible) {
                        fadeIn(Duration.FADE_IN)
                    }
                }

                with(dropdownMenuChapter) {
                    if (isInvisible) {
                        fadeIn(Duration.FADE_IN)
                    }
                }
            }

            currentChapter = it
        })
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
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
        setup(currentBook, currentChapter)
    }

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.bible.views"

        private object Key {
            private const val OBJECT_NAME = "Key"
            const val RECREATE = "$PACKAGE_NAME.$OBJECT_NAME.RECREATE"
        }
    }
}