package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import androidx.lifecycle.*
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.VerseAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Position
import com.duke.orca.android.kotlin.biblelockscreen.bible.models.entries.Verse
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BookRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.PositionRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.VerseRepository
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val positionRepository: PositionRepository,
    private val verseRepository: VerseRepository
) : ViewModel() {
    private val favorites = MutableLiveData<List<Verse>>()
    private val nativeAds = MutableLiveData<List<NativeAd>>()

    val adapterItems = MediatorLiveData<List<VerseAdapter.AdapterItem>>().apply {
        addSource(favorites) {
            value = combine(favorites, nativeAds)
        }

        addSource(nativeAds) {
            value = combine(favorites, nativeAds)
        }
    }

    val bibleBook by lazy { bookRepository.get() }

    fun loadFavorites() {
        viewModelScope.launch {
            verseRepository.getFavorites().collect {
                favorites.value = it
            }
        }
    }

    fun insertPosition(position: Position) = runBlocking {
        positionRepository.insert(position)
    }

    fun updateFavorites(id: Int, favorites: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            verseRepository.updateFavorite(id, favorites)
        }
    }

    private fun combine(
        source1: LiveData<List<Verse>>,
        source2: LiveData<List<NativeAd>>
    ): List<VerseAdapter.AdapterItem> {
        val bibleVerses = source1.value ?: emptyList()
        val nativeAds = source2.value ?: emptyList()

        val adapterItems = arrayListOf<VerseAdapter.AdapterItem>()

        adapterItems.addAll(bibleVerses.map { VerseAdapter.AdapterItem.AdapterVerse(it) })

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