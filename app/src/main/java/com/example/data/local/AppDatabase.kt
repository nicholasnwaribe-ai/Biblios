package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        HighlightEntity::class,
        CollectionEntity::class,
        SavedPassageEntity::class,
        ReadingHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bibleDao(): BibleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "biblios_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialCollections(database.bibleDao())
                    }
                }
            }

            private suspend fun populateInitialCollections(dao: BibleDao) {
                val initialCollections = listOf(
                    CollectionEntity(
                        id = 1,
                        name = "Favorites",
                        description = "Cherished verses and lifelong anchors",
                        colorHex = "#E11D48",
                        iconName = "favorite"
                    ),
                    CollectionEntity(
                        id = 2,
                        name = "Peace & Comfort",
                        description = "Scriptures for anxious thoughts and restful nights",
                        colorHex = "#2563EB",
                        iconName = "spa"
                    ),
                    CollectionEntity(
                        id = 3,
                        name = "Faith & Promises",
                        description = "Unshakable covenants and declarations of hope",
                        colorHex = "#D97706",
                        iconName = "stars"
                    ),
                    CollectionEntity(
                        id = 4,
                        name = "Daily Wisdom",
                        description = "Proverbs and practical guidance for everyday life",
                        colorHex = "#059669",
                        iconName = "lightbulb"
                    )
                )
                for (col in initialCollections) {
                    dao.insertCollection(col)
                }

                // Pre-save John 3:16 in Favorites
                dao.insertSavedPassage(
                    SavedPassageEntity(
                        collectionId = 1,
                        bookId = 43,
                        bookName = "John",
                        chapter = 3,
                        startVerse = 16,
                        endVerse = 16,
                        translation = "NIV",
                        passageText = "For God so loved the world that he gave his one and only Son, that whoever believes in him shall not perish but have eternal life.",
                        note = "The gospel in a single verse."
                    )
                )

                // Pre-save Psalm 23:1 in Peace & Comfort
                dao.insertSavedPassage(
                    SavedPassageEntity(
                        collectionId = 2,
                        bookId = 19,
                        bookName = "Psalms",
                        chapter = 23,
                        startVerse = 1,
                        endVerse = 3,
                        translation = "NKJV",
                        passageText = "The Lord is my shepherd; I shall not want. He makes me to lie down in green pastures; He leads me beside the still waters. He restores my soul.",
                        note = "The Shepherd provides all my needs."
                    )
                )
            }
        }
    }
}
