package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseDialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.BibleChapterAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodel.BibleChapterPagerViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentBookmarksDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarksDialogFragment : BaseDialogFragment<FragmentBookmarksDialogBinding>(),
    BibleChapterAdapter.OnItemClickListener {
    override val setWindowAnimation: Boolean
        get() = true

    private val viewModel by viewModels<BibleChapterPagerViewModel>()
    private val bibleChapterAdapter by lazy { BibleChapterAdapter(requireContext()) }

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
        delayOnLifecycle(Duration.SHORT) {
            dismiss()
        }
    }

    override fun onIconClick(item: BibleChapter) {
        viewModel.updateBookmark(item.id, item.bookmark.not())
    }

    private fun observe() {
        viewModel.getBookmarks().observe(viewLifecycleOwner, {
            bibleChapterAdapter.submitList(it)
        })
    }

    private fun bind() {
        bibleChapterAdapter.setOnItemClickListener(this)

        viewBinding.recyclerView.apply {
            adapter = bibleChapterAdapter
            layoutManager = LinearLayoutManagerWrapper(requireContext())
            scheduleLayoutAnimation()
        }
    }
}