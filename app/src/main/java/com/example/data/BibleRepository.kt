package com.example.data

import com.example.data.local.BibleDao
import com.example.data.local.CollectionEntity
import com.example.data.local.HighlightEntity
import com.example.data.local.ReadingHistoryEntity
import com.example.data.local.SavedPassageEntity
import com.example.model.BibleBook
import com.example.model.Translation
import com.example.model.Verse
import kotlinx.coroutines.flow.Flow

class BibleRepository(private val bibleDao: BibleDao) {

    fun getBooks(): List<BibleBook> = BibleCanon.books

    fun getBook(bookId: Int): BibleBook = BibleCanon.getBookById(bookId)

    fun getVersesForChapter(bookId: Int, chapter: Int, translation: Translation): List<Verse> {
        return BibleScriptureData.getVerses(bookId, chapter, translation)
    }

    // --- Highlights ---
    val allHighlights: Flow<List<HighlightEntity>> = bibleDao.getAllHighlights()

    fun getHighlightsForChapter(bookId: Int, chapter: Int): Flow<List<HighlightEntity>> {
        return bibleDao.getHighlightsForChapter(bookId, chapter)
    }

    suspend fun saveHighlight(
        bookId: Int,
        chapter: Int,
        verse: Int,
        translation: String,
        colorHex: String,
        verseText: String,
        bookName: String
    ): Long {
        return bibleDao.insertHighlight(
            HighlightEntity(
                bookId = bookId,
                chapter = chapter,
                verse = verse,
                translation = translation,
                colorHex = colorHex,
                verseText = verseText,
                bookName = bookName
            )
        )
    }

    suspend fun removeHighlight(bookId: Int, chapter: Int, verse: Int) {
        bibleDao.removeHighlight(bookId, chapter, verse)
    }

    suspend fun deleteHighlight(highlight: HighlightEntity) {
        bibleDao.deleteHighlight(highlight)
    }

    // --- Collections ---
    val allCollections: Flow<List<CollectionEntity>> = bibleDao.getAllCollections()

    suspend fun createCollection(name: String, description: String, colorHex: String, iconName: String): Long {
        return bibleDao.insertCollection(
            CollectionEntity(
                name = name,
                description = description,
                colorHex = colorHex,
                iconName = iconName
            )
        )
    }

    suspend fun deleteCollection(collection: CollectionEntity) {
        bibleDao.deletePassagesInCollection(collection.id)
        bibleDao.deleteCollection(collection)
    }

    // --- Saved Passages ---
    val allSavedPassages: Flow<List<SavedPassageEntity>> = bibleDao.getAllSavedPassages()

    fun getPassagesForCollection(collectionId: Long): Flow<List<SavedPassageEntity>> {
        return bibleDao.getPassagesForCollection(collectionId)
    }

    suspend fun savePassageToCollection(
        collectionId: Long,
        bookId: Int,
        bookName: String,
        chapter: Int,
        startVerse: Int,
        endVerse: Int,
        translation: String,
        passageText: String,
        note: String
    ): Long {
        return bibleDao.insertSavedPassage(
            SavedPassageEntity(
                collectionId = collectionId,
                bookId = bookId,
                bookName = bookName,
                chapter = chapter,
                startVerse = startVerse,
                endVerse = endVerse,
                translation = translation,
                passageText = passageText,
                note = note
            )
        )
    }

    suspend fun deleteSavedPassage(passage: SavedPassageEntity) {
        bibleDao.deleteSavedPassage(passage)
    }

    // --- Reading History ---
    val lastReadLocation: Flow<ReadingHistoryEntity?> = bibleDao.getLastReadLocation()

    suspend fun updateLastRead(bookId: Int, bookName: String, chapter: Int, verse: Int, translation: String) {
        bibleDao.updateLastReadLocation(
            ReadingHistoryEntity(
                id = 1,
                bookId = bookId,
                bookName = bookName,
                chapter = chapter,
                verse = verse,
                translation = translation
            )
        )
    }

    // --- Search ---
    fun searchVerses(query: String, translation: Translation): List<Pair<BibleBook, Verse>> {
        if (query.isBlank()) return emptyMap<BibleBook, Verse>().toList()
        val cleanQ = query.trim().lowercase()
        val results = mutableListOf<Pair<BibleBook, Verse>>()

        // Search through popular chapters first
        val searchTargets = listOf(
            Pair(1, 1), Pair(19, 23), Pair(19, 91), Pair(20, 3),
            Pair(40, 5), Pair(40, 6), Pair(43, 1), Pair(43, 3),
            Pair(45, 8), Pair(46, 13), Pair(50, 4), Pair(66, 21)
        )

        for ((bookId, ch) in searchTargets) {
            val book = BibleCanon.getBookById(bookId)
            val verses = BibleScriptureData.getVerses(bookId, ch, translation)
            for (v in verses) {
                if (v.text.lowercase().contains(cleanQ)) {
                    results.add(Pair(book, v))
                }
            }
        }
        return results
    }
}
