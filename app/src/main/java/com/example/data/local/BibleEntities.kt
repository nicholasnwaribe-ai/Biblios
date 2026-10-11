package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "highlights")
data class HighlightEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Int,
    val chapter: Int,
    val verse: Int,
    val translation: String,
    val colorHex: String,
    val verseText: String,
    val bookName: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val colorHex: String = "#3B82F6",
    val iconName: String = "bookmark",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_passages")
data class SavedPassageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val collectionId: Long,
    val bookId: Int,
    val bookName: String,
    val chapter: Int,
    val startVerse: Int,
    val endVerse: Int = startVerse,
    val translation: String,
    val passageText: String,
    val note: String = "",
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reading_history")
data class ReadingHistoryEntity(
    @PrimaryKey val id: Int = 1, // Single record for last read location
    val bookId: Int,
    val bookName: String,
    val chapter: Int,
    val verse: Int,
    val translation: String,
    val updatedAt: Long = System.currentTimeMillis()
)
