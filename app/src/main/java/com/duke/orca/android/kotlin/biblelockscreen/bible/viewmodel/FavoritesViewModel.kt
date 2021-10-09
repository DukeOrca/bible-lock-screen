package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodel

import androidx.lifecycle.*
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.BibleVerseAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.repository.BibleVerseRepository
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val repository: BibleVerseRepository) : ViewModel() {
    private val favorites = MutableLiveData<List<BibleVerse>>()
    private val nativeAds = MutableLiveData<List<NativeAd>>()

    val adapterItems = MediatorLiveData<List<BibleVerseAdapter.AdapterItem>>().apply {
        addSource(favorites) {
            value = combine(favorites, nativeAds)
        }

        addSource(nativeAds) {
            value = combine(favorites, nativeAds)
        }
    }

    fun getFavorites() {
        viewModelScope.launch {
            repository.getFavorites().collect {
                favorites.value = it
            }
        }
    }

    fun updateFavorites(id: Int, favorites: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFavorites(id, favorites)
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