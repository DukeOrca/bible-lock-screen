package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import androidx.lifecycle.*
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.VerseAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleBookRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleVerseRepository
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val bibleBookRepository: BibleBookRepository,
    private val bibleVerseRepository: BibleVerseRepository
) : ViewModel() {
    private val favorites = MutableLiveData<List<BibleVerse>>()
    private val nativeAds = MutableLiveData<List<NativeAd>>()

    val adapterItems = MediatorLiveData<List<VerseAdapter.AdapterItem>>().apply {
        addSource(favorites) {
            value = combine(favorites, nativeAds)
        }

        addSource(nativeAds) {
            value = combine(favorites, nativeAds)
        }
    }

    val bibleBook by lazy { bibleBookRepository.get() }

    fun getFavorites() {
        viewModelScope.launch {
            bibleVerseRepository.getFavorites().collect {
                favorites.value = it
            }
        }
    }

    fun updateFavorites(id: Int, favorites: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            bibleVerseRepository.updateFavorites(id, favorites)
        }
    }

    private fun combine(
        source1: LiveData<List<BibleVerse>>,
        source2: LiveData<List<NativeAd>>
    ): List<VerseAdapter.AdapterItem> {
        val bibleVerses = source1.value ?: emptyList()
        val nativeAds = source2.value ?: emptyList()

        val adapterItems = arrayListOf<VerseAdapter.AdapterItem>()

        adapterItems.addAll(bibleVerses.map { VerseAdapter.AdapterItem.AdapterBibleVerse(it) })

        for (i in 0 until nativeAds.count()) {
            if (i == 0) {
                adapterItems.add(0, VerseAdapter.AdapterItem.AdapterNativeAd(-1, nativeAds[0]))
            } else {
                val index = i * AD_INTERVAL

                if (index <= adapterItems.count()) {
                    adapterItems.add(
                        index,
                        VerseAdapter.AdapterItem.AdapterNativeAd(-index, nativeAds[i])
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