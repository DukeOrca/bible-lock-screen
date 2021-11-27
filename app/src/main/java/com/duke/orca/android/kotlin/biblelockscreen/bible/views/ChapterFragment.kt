package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeIn
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseViewStubFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.VerseAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.WordAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.copyToClipboard
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BookChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.share
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels.BibleChapterViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentChapterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

@FlowPreview
@AndroidEntryPoint
class ChapterFragment : BaseViewStubFragment(),
    VerseAdapter.OnIconClickListener,
    OptionChoiceDialogFragment.OnOptionChoiceListener
{
    override val layoutResource: Int
        get() = R.layout.fragment_chapter

    private val viewModel by viewModels<BibleChapterViewModel>()
    private val bookChapter by lazy { arguments?.getParcelable<BookChapter>(Key.BOOK_CHAPTER) }
    private val book by lazy { bookChapter?.book ?: 1 }
    private val chapter by lazy { bookChapter?.chapter ?: 1 }

    private val wordAdapter by lazy {
        WordAdapter(requireContext()).apply {
            runBlocking {
                setFont(viewModel.font.first())
            }

            setOnOptionsItemSelectedListener(object : WordAdapter.OnOptionsItemSelectedListener {
                override fun onOptionsItemSelected(
                    item: WordAdapter.AdapterItem.Word,
                    optionsItem: WordAdapter.OptionsItem
                ) {
                    when(optionsItem) {
                        is WordAdapter.OptionsItem.Highlight -> {}
                        is WordAdapter.OptionsItem.HighlightColor -> {}
                        is WordAdapter.OptionsItem.Bookmark -> {}
                        is WordAdapter.OptionsItem.Favorite -> viewModel.updateFavorites(item.id, item.favorite.not())
                        is WordAdapter.OptionsItem.More -> {}
                    }
                }
            })
        }
    }
    private val options by lazy { arrayOf(getString(R.string.copy), getString(R.string.share)) }

    private var bibleChapter: BibleChapter? = null

    private var _binding: FragmentChapterBinding? = null
    private val binding: FragmentChapterBinding get() = _binding!!

    private val scrolledToPosition = AtomicBoolean(false)

    override fun onInflated(view: View) {
        _binding = FragmentChapterBinding.bind(view)

        binding.recyclerViewBibleChapter.apply {
            adapter = wordAdapter
            layoutManager = LinearLayoutManagerWrapper(requireContext())
            setHasFixedSize(true)

            with(itemAnimator) {
                if (this is SimpleItemAnimator) {
                    supportsChangeAnimations = false
                }
            }
        }

        viewModel.getBibleChapter(book, chapter)

        viewModel.bibleChapter.observe(viewLifecycleOwner, {
            bibleChapter = it

            if (scrolledToPosition.compareAndSet(false, true)) {
                delayOnLifecycle(Duration.SHORT) {
                    binding.recyclerViewBibleChapter.scrollToPosition(it.position)
                    binding.root.fadeIn(Duration.FADE_IN)
                }
            }
        })

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.font.distinctUntilChanged().collectLatest {
                wordAdapter.setFont(it)
            }
        }
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

    override fun onStop() {
        savePosition()
        super.onStop()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initData() {
        viewModel.getBibleChapter(book, chapter)
        viewModel.getVerses(book, chapter)
        viewModel.getSubVerses(book, chapter)
    }

    private fun observe() {
        viewModel.adapterItems.observe(viewLifecycleOwner, {
            wordAdapter.submitList(it)
        })
    }

    private fun savePosition() {
        try {
            val layoutManager = binding.recyclerViewBibleChapter.layoutManager

            if (layoutManager is LinearLayoutManager) {
                val position = layoutManager.findLastCompletelyVisibleItemPosition()

                bibleChapter?.let {
                    viewModel.updatePosition(it.id, position)
                }
            }
        } catch (e: NullPointerException) {
            Timber.e(e)
        }
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
                bibleVerse?.let { copyToClipboard(requireContext(), viewModel.book, it) }
                delayOnLifecycle(Duration.Delay.DISMISS) {
                    dialogFragment.dismiss()
                }
            }
            options[1] -> {
                bibleVerse?.let { share(requireContext(), viewModel.book, it) }
                delayOnLifecycle(Duration.Delay.DISMISS) {
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

        fun newInstance(bookChapter: BookChapter): ChapterFragment {
            return ChapterFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Key.BOOK_CHAPTER, bookChapter)
                }
            }
        }
    }
}