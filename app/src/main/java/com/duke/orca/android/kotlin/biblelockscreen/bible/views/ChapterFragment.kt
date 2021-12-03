package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.`is`
import com.duke.orca.android.kotlin.biblelockscreen.application.color.view.ColorPickerDialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeIn
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseViewStubFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.VerseAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.WordAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.copyToClipboard
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Verse
import com.duke.orca.android.kotlin.biblelockscreen.bible.share
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels.ChapterPagerViewModel
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels.ChapterViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentChapterBinding
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import java.util.concurrent.atomic.AtomicBoolean

@FlowPreview
@AndroidEntryPoint
class ChapterFragment : BaseViewStubFragment(),
    ColorPickerDialogFragment.OnColorPickedListener,
    VerseAdapter.OnIconClickListener,
    OptionChoiceDialogFragment.OnOptionChoiceListener
{
    override val layoutResource: Int
        get() = R.layout.fragment_chapter

    private val activityViewModel by activityViewModels<ChapterPagerViewModel>()
    private val viewModel by viewModels<ChapterViewModel>()
    private val book by lazy { arguments?.getInt(Key.BOOK) ?: 1 }
    private val chapter by lazy { arguments?.getInt(Key.CHAPTER) ?: 1 }

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
                        is WordAdapter.OptionsItem.Highlight -> viewModel.updateHighlightColor(item.id, optionsItem.highlightColor)
                        is WordAdapter.OptionsItem.HighlightColor -> {
                            ColorPickerDialogFragment().also {
                                it.show(childFragmentManager, it.tag)
                            }
                        }
                        is WordAdapter.OptionsItem.Bookmark -> viewModel.updateBookmark(item.id, optionsItem.liked)
                        is WordAdapter.OptionsItem.Favorite -> viewModel.updateFavorite(item.id, optionsItem.liked)
                        is WordAdapter.OptionsItem.More -> {
                            OptionChoiceDialogFragment.newInstance(options, item.toVerse()).also {
                                it.show(childFragmentManager, it.tag)
                            }
                        }
                    }
                }
            })
        }
    }

    private val options by lazy { arrayOf(getString(R.string.copy), getString(R.string.share)) }

    private var _binding: FragmentChapterBinding? = null
    private val binding: FragmentChapterBinding get() = _binding!!

    private val scrolledToPosition = AtomicBoolean(false)

    private var dy = 0

    override fun onInflate(view: View) {
        _binding = FragmentChapterBinding.bind(view)

        binding.recyclerViewChapter.apply {
            adapter = wordAdapter
            layoutManager = LinearLayoutManagerWrapper(requireContext())
            setHasFixedSize(true)

            with(itemAnimator) {
                if (this is SimpleItemAnimator) {
                    supportsChangeAnimations = false
                }
            }


            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    this@ChapterFragment.dy += dy
                }
            })
        }

        if (scrolledToPosition.compareAndSet(false, true)) {

            val recentlyRead = runBlocking {
                activityViewModel.recentlyReadDataStore.data.first()
            }

            if (recentlyRead.book.`is`(book) && recentlyRead.chapter.`is`(chapter)) {
                binding.recyclerViewChapter.smoothScrollBy(0, activityViewModel.dy)
            }

            delayOnLifecycle(Duration.SHORT) {
                binding.root.fadeIn(Duration.FADE_IN)
            }
        }

        viewModel.highlightColor.observe(viewLifecycleOwner, {
            wordAdapter.setHighlightColor(it)
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    override fun onColorPicked(dialogFragment: DialogFragment, @ColorInt color: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            DataStore.HighlightColor.putHighlightColor(requireContext(), color)
            delay(Duration.Delay.DISMISS)
            dialogFragment.dismiss()
        }
    }

    private fun initData() {
        viewModel.getVerses(book, chapter)
        viewModel.getSubVerses(book, chapter)
    }

    private fun observe() {
        viewModel.adapterItems.observe(viewLifecycleOwner, {
            wordAdapter.submitList(it)
        })
    }

    override fun onFavoriteClick(verse: Verse, favorites: Boolean) {
        viewModel.updateFavorite(verse.id, favorites)
    }

    override fun onMoreVertClick(verse: Verse) {
        OptionChoiceDialogFragment.newInstance(options, verse).also {
            it.show(childFragmentManager, it.tag)
        }
    }

    override fun onOptionChoice(
        dialogFragment: DialogFragment,
        option: String,
        verse: Verse?
    ) {
        when(option) {
            options[0] -> {
                verse?.let { copyToClipboard(requireContext(), viewModel.book, it) }
                delayOnLifecycle(Duration.Delay.DISMISS) {
                    dialogFragment.dismiss()
                }
            }
            options[1] -> {
                verse?.let { share(requireContext(), viewModel.book, it) }
                delayOnLifecycle(Duration.Delay.DISMISS) {
                    dialogFragment.dismiss()
                }
            }
        }
    }

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.bible.views"

        private object Key {
            private const val OBJECT_NAME = "Key"
            const val BOOK = "$PACKAGE_NAME.$OBJECT_NAME.BOOK"
            const val CHAPTER = "$PACKAGE_NAME.$OBJECT_NAME.CHAPTER"
        }

        fun newInstance(book: Int, chapter: Int): ChapterFragment {
            return ChapterFragment().apply {
                arguments = Bundle().apply {
                    putInt(Key.BOOK, book)
                    putInt(Key.CHAPTER, chapter)
                }
            }
        }
    }
}