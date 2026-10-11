package com.example.model

enum class Translation(
    val code: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val copyrightNotice: String
) {
    NIV(
        code = "NIV",
        title = "New International Version",
        subtitle = "Modern, balanced & widely read",
        description = "A thought-for-thought and word-for-word balanced translation by biblical scholars.",
        copyrightNotice = "New International Version (NIV) © Biblica, Inc."
    ),
    NKJV(
        code = "NKJV",
        title = "New King James Version",
        subtitle = "Classic majesty in modern English",
        description = "Retains the traditional poetic cadence and sentence structure of the KJV while updating archaic vocabulary.",
        copyrightNotice = "New King James Version (NKJV) © Thomas Nelson"
    ),
    KJV(
        code = "KJV",
        title = "King James Version (1611)",
        subtitle = "Public domain classic & historic literary majesty",
        description = "The foundational English translation commissioned by King James I of England in 1611.",
        copyrightNotice = "Public Domain"
    ),
    GNT(
        code = "GNT",
        title = "Good News Translation (Good News)",
        subtitle = "Clear, dynamic equivalence for contemporary readers",
        description = "Also known as Today's English Version, known for easy reading and clear contemporary expression.",
        copyrightNotice = "Good News Translation (GNT) © American Bible Society"
    );

    companion object {
        fun fromCode(code: String): Translation =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: KJV
    }
}

enum class Testament(val label: String) {
    OLD("Old Testament"),
    NEW("New Testament")
}

data class BibleBook(
    val id: Int,
    val name: String,
    val shortName: String,
    val testament: Testament,
    val category: String,
    val totalChapters: Int
)

data class Verse(
    val bookId: Int,
    val chapter: Int,
    val verse: Int,
    val text: String,
    val isWordsOfChrist: Boolean = false
)

data class OpenedPassageItem(
    val id: String,
    val book: BibleBook,
    val chapter: Int,
    val verse: Int,
    val text: String,
    val translation: Translation
)

data class HighlightColor(
    val name: String,
    val hex: String,
    val displayColorHex: Long,
    val textColorHex: Long
)

val PRESET_HIGHLIGHT_COLORS = listOf(
    HighlightColor("Amber Gold", "#FFE082", 0xFFFFE082, 0xFF4A3800),
    HighlightColor("Emerald Green", "#A5D6A7", 0xFFA5D6A7, 0xFF1B4D24),
    HighlightColor("Sky Blue", "#90CAF9", 0xFF90CAF9, 0xFF0D3D66),
    HighlightColor("Rose Pink", "#F48FB1", 0xFFF48FB1, 0xFF631737),
    HighlightColor("Soft Purple", "#CE93D8", 0xFFCE93D8, 0xFF4A1454)
)

data class ReaderSettings(
    val fontSizeSp: Float = 18f,
    val lineSpacingMultiplier: Float = 1.4f,
    val fontStyle: ReaderFont = ReaderFont.SERIF,
    val readingTheme: ReadingTheme = ReadingTheme.WARM_SEPIA,
    val showVerseNumbers: Boolean = true,
    val wordsOfChristRed: Boolean = true
)

enum class ReaderFont(val label: String) {
    SERIF("Classic Serif"),
    SANS("Modern Sans"),
    MONO("Monospace")
}

enum class ReadingTheme(val label: String, val bgHex: Long, val textHex: Long, val cardHex: Long) {
    WARM_SEPIA("Sepia Parchment", 0xFFF9F5EC, 0xFF2C2418, 0xFFEFE9DB),
    CLEAN_LIGHT("Daylight White", 0xFFFFFFFF, 0xFF1C1B1F, 0xFFF3F3F7),
    MIDNIGHT("Midnight Navy", 0xFF111827, 0xFFE2E8F0, 0xFF1F2937),
    AMOLED_BLACK("OLED Black", 0xFF000000, 0xFFE6E6E6, 0xFF121212)
}
