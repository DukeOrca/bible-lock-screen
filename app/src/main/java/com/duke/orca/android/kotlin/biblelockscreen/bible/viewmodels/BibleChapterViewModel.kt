package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import android.app.Application
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.BLANK
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.WordAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.SubBookDatasourceImpl
import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.SubVerseDatasourceImpl
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Font
import com.duke.orca.android.kotlin.biblelockscreen.bible.repositories.*
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.datastore.PreferencesKeys
import com.duke.orca.android.kotlin.biblelockscreen.datastore.dataStore
import com.duke.orca.android.kotlin.biblelockscreen.persistence.database.SubDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BibleChapterViewModel @Inject constructor(
    private val bibleBookRepository: BibleBookRepository,
    private val bibleChapterRepository: BibleChapterRepository,
    private val bibleVerseRepository: BibleVerseRepository,
    application: Application
) : AndroidViewModel(application) {
    private val dataStore = application.dataStore

    private val subDatabase = SubDatabase.getInstance(application)
    private val subBookRepository by lazy {
        subDatabase?.let {
            SubBookRepositoryImpl(SubBookDatasourceImpl(it))
        }
    }
    private val subVerseRepository by lazy {
        subDatabase?.let {
            SubVerseRepositoryImpl(SubVerseDatasourceImpl(it))
        }
    }

    private val verses = MutableLiveData<List<BibleVerse>>()
    private val subVerses = MutableLiveData<List<BibleVerse>>()

    val adapterItems = MediatorLiveData<List<WordAdapter.AdapterItem>>().apply {
        addSource(verses) {
            value = combine(verses, subVerses)
        }

        addSource(subVerses) {
            value = combine(verses, subVerses)
        }
    }

    val book by lazy { bibleBookRepository.get() }
    private val subBook by lazy { subBookRepository?.get() }

    private val _bibleChapter = MutableLiveData<BibleChapter>()
    val bibleChapter: LiveData<BibleChapter> = _bibleChapter

    val font = dataStore.data.mapLatest {
        val size = it[PreferencesKeys.Font.Bible.size] ?: DataStore.Font.DEFAULT_FONT_SIZE
        val textAlignment = it[PreferencesKeys.Font.Bible.textAlignment] ?: DataStore.Font.TextAlignment.LEFT

        Font(
            size = size,
            textAlignment = textAlignment
        )
    }

    fun getBibleChapter(book: Int, chapter: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            bibleChapterRepository.get(book, chapter).collect {
                _bibleChapter.postValue(it)
            }
        }
    }

    fun getVerses(book: Int, chapter: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            bibleVerseRepository.get(book, chapter).collect {
                verses.postValue(it)
            }
        }
    }

    fun getSubVerses(book: Int, chapter: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            subVerseRepository?.get(book, chapter)?.collect {
                subVerses.postValue(it)
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
        source2: LiveData<List<BibleVerse>>
    ): List<WordAdapter.AdapterItem> {
        val verses = source1.value ?: emptyList()
        val subVerses = source2.value ?: emptyList()

        val adapterItems = arrayListOf<WordAdapter.AdapterItem>()
        val words = if (subVerses.isNotEmpty()) {
            verses.zip(subVerses) { verse, subVerse ->
                WordAdapter.AdapterItem.Word(
                    id = verse.id,
                    book = WordAdapter.AdapterItem.Word.Book(
                        verse.book,
                        book.name(verse.book)
                    ),
                    subBook = WordAdapter.AdapterItem.Word.Book(
                        subVerse.book,
                        subBook?.name(subVerse.book) ?: BLANK
                    ),
                    verse = verse.verse,
                    word = verse.word,
                    subWord = subVerse.word,
                    bookmark = verse.bookmark,
                    color = -1,
                    favorite = verse.favorites
                )
            }
        } else {
            verses.map { verse ->
                WordAdapter.AdapterItem.Word(
                    id = verse.id,
                    book = WordAdapter.AdapterItem.Word.Book(
                        verse.book,
                        book.name(verse.book)
                    ),
                    verse = verse.verse,
                    word = verse.word,
                    bookmark = verse.bookmark,
                    color = -1,
                    favorite = verse.favorites
                )
            }
        }

        adapterItems.addAll(words)

        return adapterItems
    }
}