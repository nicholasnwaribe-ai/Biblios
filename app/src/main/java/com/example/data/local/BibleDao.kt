package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BibleDao {

    // --- Highlights ---
    @Query("SELECT * FROM highlights ORDER BY createdAt DESC")
    fun getAllHighlights(): Flow<List<HighlightEntity>>

    @Query("SELECT * FROM highlights WHERE bookId = :bookId AND chapter = :chapter")
    fun getHighlightsForChapter(bookId: Int, chapter: Int): Flow<List<HighlightEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighlight(highlight: HighlightEntity): Long

    @Query("DELETE FROM highlights WHERE bookId = :bookId AND chapter = :chapter AND verse = :verse")
    suspend fun removeHighlight(bookId: Int, chapter: Int, verse: Int)

    @Delete
    suspend fun deleteHighlight(highlight: HighlightEntity)

    // --- Collections ---
    @Query("SELECT * FROM collections ORDER BY createdAt ASC")
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity): Long

    @Update
    suspend fun updateCollection(collection: CollectionEntity)

    @Delete
    suspend fun deleteCollection(collection: CollectionEntity)

    @Query("SELECT * FROM collections WHERE id = :id LIMIT 1")
    suspend fun getCollectionById(id: Long): CollectionEntity?

    // --- Saved Passages in Collections ---
    @Query("SELECT * FROM saved_passages WHERE collectionId = :collectionId ORDER BY savedAt DESC")
    fun getPassagesForCollection(collectionId: Long): Flow<List<SavedPassageEntity>>

    @Query("SELECT * FROM saved_passages ORDER BY savedAt DESC")
    fun getAllSavedPassages(): Flow<List<SavedPassageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedPassage(passage: SavedPassageEntity): Long

    @Delete
    suspend fun deleteSavedPassage(passage: SavedPassageEntity)

    @Query("DELETE FROM saved_passages WHERE collectionId = :collectionId")
    suspend fun deletePassagesInCollection(collectionId: Long)

    @Query("SELECT COUNT(*) FROM saved_passages WHERE collectionId = :collectionId")
    fun getPassageCountForCollection(collectionId: Long): Flow<Int>

    // --- Reading History ---
    @Query("SELECT * FROM reading_history WHERE id = 1 LIMIT 1")
    fun getLastReadLocation(): Flow<ReadingHistoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLastReadLocation(history: ReadingHistoryEntity)
}
