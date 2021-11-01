package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodel

import android.app.Application
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.*
import com.duke.orca.android.kotlin.biblelockscreen.base.viewmodel.BaseViewModel
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.BibleVerseAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleBookRepository
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.BibleVerseRepository
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.datastore.PreferencesKeys
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BibleVerseViewModel @Inject constructor(
    private val bibleBookRepository: BibleBookRepository,
    private val bibleVerseRepository: BibleVerseRepository,
    application: Application
) : BaseViewModel(application) {
    private val bibleVerse = MutableLiveData<BibleVerse>()

    val bibleBook by lazy { bibleBookRepository.get() }

    val pair = MediatorLiveData<Pair<BibleVerse, AttributeSet>?>().apply {
        addSource(attributeSet) {
            value = combine(attributeSet, bibleVerse)
        }

        addSource(bibleVerse) {
            value = combine(attributeSet, bibleVerse)
        }
    }

    private fun combine(
        source1: LiveData<AttributeSet>,
        source2: LiveData<BibleVerse>
    ): Pair<BibleVerse, AttributeSet>? {
        val attributeSet = source1.value ?: return null
        val bibleVerse = source2.value ?: return null

        return bibleVerse to attributeSet
    }

    suspend fun get(id: Int) {
        bibleVerseRepository.get(id).flowOn(Dispatchers.IO).collect {
            bibleVerse.postValue(it)
        }
    }

    fun updateFavorites(id: Int, favorites: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            bibleVerseRepository.updateFavorites(id, favorites)
        }
    }
}

data class AttributeSet (
    val bold: Boolean,
    val fontSize: Float,
    val textAlignment: Int
)