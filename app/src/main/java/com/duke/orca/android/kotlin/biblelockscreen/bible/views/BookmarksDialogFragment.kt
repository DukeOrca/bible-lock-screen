package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeIn
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseDialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.ChapterAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels.BibleChapterPagerViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentBookmarksDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarksDialogFragment : BaseDialogFragment<FragmentBookmarksDialogBinding>(),
    ChapterAdapter.OnItemClickListener {
    override val setWindowAnimation: Boolean
        get() = true

    private val viewModel by viewModels<BibleChapterPagerViewModel>()
    private val chapterAdapter by lazy { ChapterAdapter(requireContext(), viewModel.bibleBook) }

    private var onBookmarkClickListener: OnBookmarkClickListener? = null

    interface OnBookmarkClickListener {
        fun onBookmarkClick(id: Int)
    }

    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBookmarksDialogBinding {
        return FragmentBookmarksDialogBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(parentFragment) {
            if (this is OnBookmarkClickListener) {
                onBookmarkClickListener = this
            }
        }
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

    override fun onItemClick(item: BibleChapter) {
        onBookmarkClickListener?.onBookmarkClick(item.id)
        delayOnLifecycle(Duration.Delay.DISMISS) {
            dismiss()
        }
    }

    override fun onIconClick(item: BibleChapter) {
        viewModel.updateBookmark(item.id, item.bookmark.not())
    }

    private fun observe() {
        viewModel.getBookmarks().observe(viewLifecycleOwner, {
            chapterAdapter.submitList(it) {
                delayOnLifecycle(Duration.SHORT) {
                    if (it.isEmpty()) {
                        viewBinding.linearLayout.fadeIn(Duration.FADE_IN)
                    } else {
                        with(viewBinding.recyclerView) {
                            if (isInvisible) {
                                fadeIn(Duration.FADE_IN)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun bind() {
        chapterAdapter.setOnItemClickListener(this)

        viewBinding.recyclerView.apply {
            adapter = chapterAdapter
            layoutManager = LinearLayoutManagerWrapper(requireContext())
            setHasFixedSize(true)
        }
    }
}