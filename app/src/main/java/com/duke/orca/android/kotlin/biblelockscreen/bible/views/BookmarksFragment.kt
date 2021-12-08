package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Key
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.RequestKey
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeIn
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.BookmarkAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels.BookmarksViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentBookmarksBinding
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class BookmarksFragment : BaseFragment<FragmentBookmarksBinding>(),
    BookmarkAdapter.OnItemClickListener
{
    override val toolbar: Toolbar
        get() = viewBinding.toolbar

    private val viewModel by viewModels<BookmarksViewModel>()

    private val bookmarkAdapter by lazy { BookmarkAdapter(requireContext(), bible) }
    private val fragmentResultSetRequired by lazy {
        arguments?.getBoolean(Key.FRAGMENT_RESULT_SET_REQUIRED) ?: false
    }

    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBookmarksBinding {
        return FragmentBookmarksBinding.inflate(inflater, container, false)
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

    override fun onItemClick(item: BookmarkAdapter.AdapterItem.BookmarkItem) {
        if (fragmentResultSetRequired) {
            val verse = item.verse

            viewModel.insertPosition(verse.position)

            setFragmentResult(
                RequestKey.HIGHLIGHTS_FRAGMENT,
                bundleOf(Key.VERSE to verse)
            )

            parentFragmentManager.popBackStackImmediate()
        } else {
            addFragment(
                R.id.fragment_container_view,
                parentFragmentManager,
                ChapterPagerFragment.newInstance(item.verse)
            )
        }
    }

    override fun onIconClick(item: BookmarkAdapter.AdapterItem.BookmarkItem) {
        viewModel.updateBookmark(item.id, item.verse.bookmark.not())
    }

    private fun observe() {
        compositeDisposable.add(
            viewModel.bookmarks
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe { list ->
                    val bookmarkItems = list.map { BookmarkAdapter.AdapterItem.BookmarkItem(it.id, it) }
                    val map = bookmarkItems.groupBy {
                        val name = bible.name(it.verse.book)

                        BookmarkAdapter.AdapterItem.BookItem(name = name)
                    }

                    bookmarkAdapter.submitMap(map) {
                        delayOnLifecycle(Duration.SHORT) {
                            if (map.isEmpty()) {
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
                }
        )
    }

    private fun bind() {
        bookmarkAdapter.setOnItemClickListener(this)

        viewBinding.recyclerView.apply {
            adapter = bookmarkAdapter
            layoutManager = LinearLayoutManagerWrapper(requireContext())
            setHasFixedSize(true)
        }
    }

    companion object {
        fun newInstance(fragmentResultSetRequired: Boolean): BookmarksFragment {
            return BookmarksFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(Key.FRAGMENT_RESULT_SET_REQUIRED, fragmentResultSetRequired)
                }
            }
        }
    }
}