package com.duke.orca.android.kotlin.biblelockscreen.bible.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.BookToChapters
import com.duke.orca.android.kotlin.biblelockscreen.bible.views.ChapterFragment

class ChapterPagerAdapter(fragment: Fragment, private val book: Int)
    : FragmentStateAdapter(fragment) {
    private val chapters = BookToChapters.get(book)

    override fun getItemCount(): Int = chapters.count()

    override fun createFragment(position: Int): Fragment {
        return ChapterFragment.newInstance(book, chapters[position])
    }
}