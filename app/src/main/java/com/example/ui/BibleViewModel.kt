package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BibleCanon
import com.example.data.BibleRepository
import com.example.data.local.AppDatabase
import com.example.data.local.CollectionEntity
import com.example.data.local.HighlightEntity
import com.example.data.local.SavedPassageEntity
import com.example.model.BibleBook
import com.example.model.HighlightColor
import com.example.model.OpenedPassageItem
import com.example.model.PRESET_HIGHLIGHT_COLORS
import com.example.model.ReaderSettings
import com.example.model.Translation
import com.example.model.Verse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavigationTab {
    READER,
    COLLECTIONS,
    SEARCH
}

class BibleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BibleRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = BibleRepository(database.bibleDao())
    }

    val allBooks: List<BibleBook> = repository.getBooks()

    // Navigation and reading state
    private val _currentTranslation = MutableStateFlow(Translation.NIV)
    val currentTranslation: StateFlow<Translation> = _currentTranslation.asStateFlow()

    private val _currentBook = MutableStateFlow(BibleCanon.getBookById(43)) // John
    val currentBook: StateFlow<BibleBook> = _currentBook.asStateFlow()

    private val _currentChapter = MutableStateFlow(3)
    val currentChapter: StateFlow<Int> = _currentChapter.asStateFlow()

    private val _targetVerseToScroll = MutableStateFlow<Int?>(null)
    val targetVerseToScroll: StateFlow<Int?> = _targetVerseToScroll.asStateFlow()

    // Selection for highlight/share/save
    private val _selectedVerses = MutableStateFlow<Set<Int>>(emptySet())
    val selectedVerses: StateFlow<Set<Int>> = _selectedVerses.asStateFlow()

    // Reader UI appearance
    private val _readerSettings = MutableStateFlow(ReaderSettings())
    val readerSettings: StateFlow<ReaderSettings> = _readerSettings.asStateFlow()

    // Active bottom navigation tab
    private val _activeTab = MutableStateFlow(NavigationTab.READER)
    val activeTab: StateFlow<NavigationTab> = _activeTab.asStateFlow()

    // Scroll state for morphing bottom pill into rectangle
    private val _isScrolledDown = MutableStateFlow(false)
    val isScrolledDown: StateFlow<Boolean> = _isScrolledDown.asStateFlow()

    fun setScrolledDown(scrolled: Boolean) {
        _isScrolledDown.value = scrolled
    }

    // Search query & results
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Pair<BibleBook, Verse>>>(emptyList())
    val searchResults: StateFlow<List<Pair<BibleBook, Verse>>> = _searchResults.asStateFlow()

    // Verses for current chapter
    private val _versesInChapter = MutableStateFlow<List<Verse>>(emptyList())
    val versesInChapter: StateFlow<List<Verse>> = _versesInChapter.asStateFlow()

    // Highlights for current chapter mapped by verse number
    val highlightsInChapter: StateFlow<Map<Int, HighlightEntity>> = combine(
        _currentBook,
        _currentChapter,
        repository.allHighlights
    ) { book, chapter, allHighlights ->
        allHighlights
            .filter { it.bookId == book.id && it.chapter == chapter }
            .associateBy { it.verse }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // All Highlights Flow
    val allHighlights: StateFlow<List<HighlightEntity>> = repository.allHighlights
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Collections Flow
    val collections: StateFlow<List<CollectionEntity>> = repository.allCollections
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Saved Passages Flow
    val allSavedPassages: StateFlow<List<SavedPassageEntity>> = repository.allSavedPassages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Collection for detail view
    private val _selectedCollection = MutableStateFlow<CollectionEntity?>(null)
    val selectedCollection: StateFlow<CollectionEntity?> = _selectedCollection.asStateFlow()

    // Opened passages workspace (maximum 20)
    private val _openedPassages = MutableStateFlow<List<OpenedPassageItem>>(emptyList())
    val openedPassages: StateFlow<List<OpenedPassageItem>> = _openedPassages.asStateFlow()

    init {
        loadChapterVerses()
        val john = BibleCanon.getBookById(43)
        openPassage(john, 3, 16)
        openPassage(john, 9, 11)
    }

    fun openPassage(book: BibleBook, chapter: Int, verse: Int): Boolean {
        val current = _openedPassages.value.toMutableList()
        val id = "${book.id}:$chapter:$verse"
        if (current.any { it.id == id }) {
            return true
        }
        if (current.size >= 20) {
            return false
        }
        val translation = _currentTranslation.value
        val verses = repository.getVersesForChapter(book.id, chapter, translation)
        val text = verses.find { it.verse == verse }?.text ?: "Verse $verse"
        current.add(OpenedPassageItem(id, book, chapter, verse, text, translation))
        _openedPassages.value = current
        return true
    }

    fun openMultipleReferences(input: String): Int {
        val parsed = BibleCanon.parsePassageReferences(input)
        if (parsed.isEmpty()) return 0

        var addedCount = 0
        for ((book, chapter, verse) in parsed) {
            if (_openedPassages.value.size >= 20) break
            val success = openPassage(book, chapter, verse)
            if (success) addedCount++
        }
        return addedCount
    }

    fun removeOpenedPassage(id: String) {
        _openedPassages.value = _openedPassages.value.filter { it.id != id }
    }

    fun clearAllOpenedPassages() {
        _openedPassages.value = emptyList()
    }

    private fun loadChapterVerses() {
        val verses = repository.getVersesForChapter(
            bookId = _currentBook.value.id,
            chapter = _currentChapter.value,
            translation = _currentTranslation.value
        )
        _versesInChapter.value = verses
        _selectedVerses.value = emptySet()
    }

    fun setTranslation(translation: Translation) {
        if (_currentTranslation.value != translation) {
            _currentTranslation.value = translation
            loadChapterVerses()
        }
    }

    fun selectBookAndChapter(book: BibleBook, chapter: Int, targetVerse: Int? = null) {
        _currentBook.value = book
        _currentChapter.value = chapter.coerceIn(1, book.totalChapters)
        _targetVerseToScroll.value = targetVerse
        loadChapterVerses()
        _activeTab.value = NavigationTab.READER

        viewModelScope.launch {
            repository.updateLastRead(
                bookId = book.id,
                bookName = book.name,
                chapter = _currentChapter.value,
                verse = targetVerse ?: 1,
                translation = _currentTranslation.value.code
            )
        }
    }

    fun onVerseScrolledTo() {
        _targetVerseToScroll.value = null
    }

    fun nextChapter() {
        val current = _currentChapter.value
        val book = _currentBook.value
        if (current < book.totalChapters) {
            selectBookAndChapter(book, current + 1)
        } else {
            // Go to next book if available
            val nextBookIndex = allBooks.indexOfFirst { it.id == book.id } + 1
            if (nextBookIndex < allBooks.size) {
                selectBookAndChapter(allBooks[nextBookIndex], 1)
            }
        }
    }

    fun previousChapter() {
        val current = _currentChapter.value
        val book = _currentBook.value
        if (current > 1) {
            selectBookAndChapter(book, current - 1)
        } else {
            // Go to previous book if available
            val prevBookIndex = allBooks.indexOfFirst { it.id == book.id } - 1
            if (prevBookIndex >= 0) {
                val prevBook = allBooks[prevBookIndex]
                selectBookAndChapter(prevBook, prevBook.totalChapters)
            }
        }
    }

    fun toggleVerseSelection(verseNum: Int) {
        val current = _selectedVerses.value.toMutableSet()
        if (current.contains(verseNum)) {
            current.remove(verseNum)
        } else {
            current.add(verseNum)
        }
        _selectedVerses.value = current
    }

    fun clearVerseSelection() {
        _selectedVerses.value = emptySet()
    }

    fun applyHighlightToSelected(color: HighlightColor) {
        val selected = _selectedVerses.value
        if (selected.isEmpty()) return

        val book = _currentBook.value
        val chapter = _currentChapter.value
        val verses = _versesInChapter.value
        val translationCode = _currentTranslation.value.code

        viewModelScope.launch {
            selected.forEach { vNum ->
                val vText = verses.find { it.verse == vNum }?.text ?: ""
                repository.saveHighlight(
                    bookId = book.id,
                    chapter = chapter,
                    verse = vNum,
                    translation = translationCode,
                    colorHex = color.hex,
                    verseText = vText,
                    bookName = book.name
                )
            }
            clearVerseSelection()
        }
    }

    fun removeHighlightFromSelected() {
        val selected = _selectedVerses.value
        val book = _currentBook.value
        val chapter = _currentChapter.value

        viewModelScope.launch {
            selected.forEach { vNum ->
                repository.removeHighlight(book.id, chapter, vNum)
            }
            clearVerseSelection()
        }
    }

    fun removeHighlightDirect(highlight: HighlightEntity) {
        viewModelScope.launch {
            repository.deleteHighlight(highlight)
        }
    }

    fun saveSelectedToCollection(collectionId: Long, note: String) {
        val selected = _selectedVerses.value.sorted()
        if (selected.isEmpty()) return

        val book = _currentBook.value
        val chapter = _currentChapter.value
        val verses = _versesInChapter.value
        val translationCode = _currentTranslation.value.code

        val startVerse = selected.first()
        val endVerse = selected.last()

        val passageText = verses.filter { selected.contains(it.verse) }
            .joinToString(separator = "\n") { "${it.verse} ${it.text}" }

        viewModelScope.launch {
            repository.savePassageToCollection(
                collectionId = collectionId,
                bookId = book.id,
                bookName = book.name,
                chapter = chapter,
                startVerse = startVerse,
                endVerse = endVerse,
                translation = translationCode,
                passageText = passageText,
                note = note
            )
            clearVerseSelection()
        }
    }

    fun createCollection(name: String, description: String, colorHex: String, iconName: String) {
        viewModelScope.launch {
            repository.createCollection(name, description, colorHex, iconName)
        }
    }

    fun deleteCollection(collection: CollectionEntity) {
        viewModelScope.launch {
            if (_selectedCollection.value?.id == collection.id) {
                _selectedCollection.value = null
            }
            repository.deleteCollection(collection)
        }
    }

    fun deleteSavedPassage(passage: SavedPassageEntity) {
        viewModelScope.launch {
            repository.deleteSavedPassage(passage)
        }
    }

    fun selectCollection(collection: CollectionEntity?) {
        _selectedCollection.value = collection
    }

    fun getFormattedSelectedText(): String {
        val selected = _selectedVerses.value.sorted()
        if (selected.isEmpty()) return ""

        val book = _currentBook.value
        val chapter = _currentChapter.value
        val translation = _currentTranslation.value
        val verses = _versesInChapter.value

        val ref = if (selected.size == 1) {
            "${book.name} $chapter:${selected.first()} (${translation.code})"
        } else {
            "${book.name} $chapter:${selected.first()}-${selected.last()} (${translation.code})"
        }

        val body = verses.filter { selected.contains(it.verse) }
            .joinToString(separator = " ") { "${it.verse}. ${it.text}" }

        return "\"$body\"\n— $ref"
    }

    fun getVersesForComparison(verseNumbers: Set<Int>): Map<Translation, String> {
        val book = _currentBook.value
        val chapter = _currentChapter.value
        val sortedVerses = verseNumbers.sorted()

        return Translation.entries.associateWith { translation ->
            val verses = repository.getVersesForChapter(book.id, chapter, translation)
            val matched = verses.filter { sortedVerses.contains(it.verse) }
            if (matched.isNotEmpty()) {
                matched.joinToString(separator = " ") { "${it.verse}. ${it.text}" }
            } else {
                "Text not available for this chapter."
            }
        }
    }

    fun getFormattedComparisonText(verseNumbers: Set<Int>): String {
        val book = _currentBook.value
        val chapter = _currentChapter.value
        val sorted = verseNumbers.sorted()
        val ref = if (sorted.size == 1) "${book.name} $chapter:${sorted.first()}"
        else "${book.name} $chapter:${sorted.first()}-${sorted.last()}"

        val comparison = getVersesForComparison(verseNumbers)
        val sb = StringBuilder()
        sb.append("Comparison: $ref\n\n")
        comparison.forEach { (tr, text) ->
            sb.append("${tr.code} (${tr.title}):\n\"$text\"\n\n")
        }
        return sb.toString().trim()
    }

    fun updateReaderSettings(update: (ReaderSettings) -> ReaderSettings) {
        _readerSettings.value = update(_readerSettings.value)
    }

    fun setActiveTab(tab: NavigationTab) {
        _activeTab.value = tab
    }

    fun performSearch(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            val results = repository.searchVerses(query, _currentTranslation.value)
            _searchResults.value = results
        }
    }
}
