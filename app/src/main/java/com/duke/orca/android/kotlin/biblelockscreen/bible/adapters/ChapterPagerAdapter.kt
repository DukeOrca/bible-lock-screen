package com.duke.orca.android.kotlin.biblelockscreen.bible.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.CHAPTER_COUNT
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BookChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.views.ChapterFragment
import kotlinx.coroutines.FlowPreview

class ChapterPagerAdapter(fragment: Fragment, private val bookChapterList: List<BookChapter>)
    : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = CHAPTER_COUNT

    override fun createFragment(position: Int): Fragment {
        return ChapterFragment.newInstance(bookChapterList[position])
    }
}