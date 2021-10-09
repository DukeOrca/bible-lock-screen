package com.duke.orca.android.kotlin.biblelockscreen.bible.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BookChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.views.BibleChapterFragment

class BibleChapterPagerAdapter(fragment: Fragment, private val bookChapters: List<BookChapter>) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 1189

    override fun createFragment(position: Int): Fragment {
        return BibleChapterFragment.newInstance(bookChapters[position])
    }
}