package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.BibleVerseAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleBookRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleChapterRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleVerseRepository
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BibleChapterViewModel @Inject constructor(
    private val bibleBookRepository: BibleBookRepository,
    private val bibleChapterRepository: BibleChapterRepository,
    private val bibleVerseRepository: BibleVerseRepository,
) : ViewModel() {
    private val verses = MutableLiveData<List<BibleVerse>>()
    private val subVerses = MutableLiveData<List<BibleVerse>>()
    private val nativeAds = MutableLiveData<List<NativeAd>>()

    val adapterItems = MediatorLiveData<List<BibleVerseAdapter.AdapterItem>>().apply {
        addSource(verses) {
            value = combine(verses, nativeAds)
        }

        addSource(nativeAds) {
            value = combine(verses, nativeAds)
        }
    }

    val bibleBook by lazy { bibleBookRepository.get() }

    private val _bibleChapter = MutableLiveData<BibleChapter>()
    val bibleChapter: LiveData<BibleChapter> = _bibleChapter

    fun getBibleChapter(book: Int, chapter: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            bibleChapterRepository.get(book, chapter).collect {
                _bibleChapter.postValue(it)
            }
        }
    }

    fun getBibleVerses(book: Int, chapter: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            bibleVerseRepository.get(book, chapter).collect {
                verses.postValue(it)
            }
        }
    }

    fun updateBookmark(id: Int, bookmark: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            bibleChapterRepository.updateBookmark(id, bookmark)
        }
    }

    fun updateFavorites(id: Int, favorites: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            bibleVerseRepository.updateFavorites(id, favorites)
        }
    }

    fun updatePosition(id: Int, position: Int) {
        if (position == RecyclerView.NO_POSITION) return

        viewModelScope.launch(Dispatchers.IO) {
            bibleChapterRepository.updatePosition(id, position)
        }
    }

    private fun combine(
        source1: LiveData<List<BibleVerse>>,
        source2: LiveData<List<NativeAd>>
    ): List<BibleVerseAdapter.AdapterItem> {
        val bibleVerses = source1.value ?: emptyList()
        val nativeAds = source2.value ?: emptyList()

        val adapterItems = arrayListOf<BibleVerseAdapter.AdapterItem>()

        adapterItems.addAll(bibleVerses.map { BibleVerseAdapter.AdapterItem.AdapterBibleVerse(it) })

        for (i in 0 until nativeAds.count()) {
            if (i == 0) {
                adapterItems.add(0, BibleVerseAdapter.AdapterItem.AdapterNativeAd(-1, nativeAds[0]))
            } else {
                val index = i * AD_INTERVAL

                if (index <= adapterItems.count()) {
                    adapterItems.add(
                        index,
                        BibleVerseAdapter.AdapterItem.AdapterNativeAd(-index, nativeAds[i])
                    )
                } else {
                    break
                }
            }
        }

        return adapterItems
    }

    companion object {
        private const val AD_INTERVAL = 8
    }
}