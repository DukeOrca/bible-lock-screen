package com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels

import android.app.Application
import androidx.annotation.ColorInt
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.BLANK
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.WordAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.SubBookDatasourceImpl
import com.duke.orca.android.kotlin.biblelockscreen.bible.datasource.local.SubVerseDatasourceImpl
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleChapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Verse
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
class ChapterViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val chapterRepository: ChapterRepository,
    private val verseRepository: VerseRepository,
    application: Application
) : AndroidViewModel(application) {
    private val dataStore = application.dataStore

    val highlightColor = dataStore.data.map {
        it[PreferencesKeys.HighlightColor.highlightColor] ?: DataStore.HighlightColor.DEFAULT
    }.asLiveData(Dispatchers.IO)

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

    private val verses = MutableLiveData<List<Verse>>()
    private val subVerses = MutableLiveData<List<Verse>>()

    val adapterItems = MediatorLiveData<List<WordAdapter.AdapterItem>>().apply {
        addSource(verses) {
            value = combine(verses, subVerses)
        }

        addSource(subVerses) {
            value = combine(verses, subVerses)
        }
    }

    val book by lazy { bookRepository.get() }
    private val subBook by lazy { subBookRepository?.get() }

    private val _bibleChapter = MutableLiveData<BibleChapter>()
    val bibleChapter: LiveData<BibleChapter> = _bibleChapter

    val font = dataStore.data.mapLatest {
        val size = it[PreferencesKeys.Font.Bible.size] ?: DataStore.Font.DEFAULT_SIZE
        val textAlignment = it[PreferencesKeys.Font.Bible.textAlignment] ?: DataStore.Font.TextAlignment.LEFT

        Font(
            bold = false,
            size = size,
            textAlignment = textAlignment
        )
    }

    fun getBibleChapter(book: Int, chapter: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            chapterRepository.get(book, chapter).collect {
                _bibleChapter.postValue(it)
            }
        }
    }

    fun getVerses(book: Int, chapter: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            verseRepository.get(book, chapter).collect {
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
            verseRepository.updateBookmark(id, bookmark)
        }
    }

    fun updateFavorite(id: Int, favorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            verseRepository.updateFavorite(id, favorite)
        }
    }

    fun updateHighlightColor(id: Int, @ColorInt highlightColor: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            verseRepository.updateHighlightColor(id, highlightColor)
        }
    }

    fun updatePosition(id: Int, position: Int) {
        if (position == RecyclerView.NO_POSITION) return

        viewModelScope.launch(Dispatchers.IO) {
            chapterRepository.updatePosition(id, position)
        }
    }

    private fun combine(
        source1: LiveData<List<Verse>>,
        source2: LiveData<List<Verse>>
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
                    favorite = verse.favorite,
                    highlightColor = verse.highlightColor
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
                    favorite = verse.favorite,
                    highlightColor = verse.highlightColor
                )
            }
        }

        adapterItems.addAll(words)

        return adapterItems
    }
}