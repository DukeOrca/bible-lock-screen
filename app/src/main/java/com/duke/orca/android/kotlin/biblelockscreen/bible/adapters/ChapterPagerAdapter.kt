package com.duke.orca.android.kotlin.biblelockscreen.bible.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BookToChapters
import com.duke.orca.android.kotlin.biblelockscreen.bible.views.ChapterFragment

class ChapterPagerAdapter(activity: FragmentActivity, private val book: Int)
    : FragmentStateAdapter(activity) {
    private val chapters = BookToChapters.get(book)

    override fun getItemCount(): Int = chapters.count()

    override fun createFragment(position: Int): Fragment {
        return ChapterFragment.newInstance(book, chapters[position])
    }
}