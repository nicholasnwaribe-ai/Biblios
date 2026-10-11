package com.example.data

import com.example.model.BibleBook
import com.example.model.Testament

object BibleCanon {

    val books: List<BibleBook> = listOf(
        // Old Testament (39 books)
        BibleBook(1, "Genesis", "Gen", Testament.OLD, "Pentateuch", 50),
        BibleBook(2, "Exodus", "Exod", Testament.OLD, "Pentateuch", 40),
        BibleBook(3, "Leviticus", "Lev", Testament.OLD, "Pentateuch", 27),
        BibleBook(4, "Numbers", "Num", Testament.OLD, "Pentateuch", 36),
        BibleBook(5, "Deuteronomy", "Deut", Testament.OLD, "Pentateuch", 34),
        BibleBook(6, "Joshua", "Josh", Testament.OLD, "Historical", 24),
        BibleBook(7, "Judges", "Judg", Testament.OLD, "Historical", 21),
        BibleBook(8, "Ruth", "Ruth", Testament.OLD, "Historical", 4),
        BibleBook(9, "1 Samuel", "1Sam", Testament.OLD, "Historical", 31),
        BibleBook(10, "2 Samuel", "2Sam", Testament.OLD, "Historical", 24),
        BibleBook(11, "1 Kings", "1Kgs", Testament.OLD, "Historical", 22),
        BibleBook(12, "2 Kings", "2Kgs", Testament.OLD, "Historical", 25),
        BibleBook(13, "1 Chronicles", "1Chr", Testament.OLD, "Historical", 29),
        BibleBook(14, "2 Chronicles", "2Chr", Testament.OLD, "Historical", 36),
        BibleBook(15, "Ezra", "Ezra", Testament.OLD, "Historical", 10),
        BibleBook(16, "Nehemiah", "Neh", Testament.OLD, "Historical", 13),
        BibleBook(17, "Esther", "Esth", Testament.OLD, "Historical", 10),
        BibleBook(18, "Job", "Job", Testament.OLD, "Poetry & Wisdom", 42),
        BibleBook(19, "Psalms", "Ps", Testament.OLD, "Poetry & Wisdom", 150),
        BibleBook(20, "Proverbs", "Prov", Testament.OLD, "Poetry & Wisdom", 31),
        BibleBook(21, "Ecclesiastes", "Eccl", Testament.OLD, "Poetry & Wisdom", 12),
        BibleBook(22, "Song of Solomon", "Song", Testament.OLD, "Poetry & Wisdom", 8),
        BibleBook(23, "Isaiah", "Isa", Testament.OLD, "Major Prophets", 66),
        BibleBook(24, "Jeremiah", "Jer", Testament.OLD, "Major Prophets", 52),
        BibleBook(25, "Lamentations", "Lam", Testament.OLD, "Major Prophets", 5),
        BibleBook(26, "Ezekiel", "Ezek", Testament.OLD, "Major Prophets", 48),
        BibleBook(27, "Daniel", "Dan", Testament.OLD, "Major Prophets", 12),
        BibleBook(28, "Hosea", "Hos", Testament.OLD, "Minor Prophets", 14),
        BibleBook(29, "Joel", "Joel", Testament.OLD, "Minor Prophets", 3),
        BibleBook(30, "Amos", "Amos", Testament.OLD, "Minor Prophets", 9),
        BibleBook(31, "Obadiah", "Obad", Testament.OLD, "Minor Prophets", 1),
        BibleBook(32, "Jonah", "Jonah", Testament.OLD, "Minor Prophets", 4),
        BibleBook(33, "Micah", "Mic", Testament.OLD, "Minor Prophets", 7),
        BibleBook(34, "Nahum", "Nah", Testament.OLD, "Minor Prophets", 3),
        BibleBook(35, "Habakkuk", "Hab", Testament.OLD, "Minor Prophets", 3),
        BibleBook(36, "Zephaniah", "Zeph", Testament.OLD, "Minor Prophets", 3),
        BibleBook(37, "Haggai", "Hag", Testament.OLD, "Minor Prophets", 2),
        BibleBook(38, "Zechariah", "Zech", Testament.OLD, "Minor Prophets", 14),
        BibleBook(39, "Malachi", "Mal", Testament.OLD, "Minor Prophets", 4),

        // New Testament (27 books)
        BibleBook(40, "Matthew", "Matt", Testament.NEW, "Gospels", 28),
        BibleBook(41, "Mark", "Mark", Testament.NEW, "Gospels", 16),
        BibleBook(42, "Luke", "Luke", Testament.NEW, "Gospels", 24),
        BibleBook(43, "John", "John", Testament.NEW, "Gospels", 21),
        BibleBook(44, "Acts", "Acts", Testament.NEW, "Church History", 28),
        BibleBook(45, "Romans", "Rom", Testament.NEW, "Epistles", 16),
        BibleBook(46, "1 Corinthians", "1Cor", Testament.NEW, "Epistles", 16),
        BibleBook(47, "2 Corinthians", "2Cor", Testament.NEW, "Epistles", 13),
        BibleBook(48, "Galatians", "Gal", Testament.NEW, "Epistles", 6),
        BibleBook(49, "Ephesians", "Eph", Testament.NEW, "Epistles", 6),
        BibleBook(50, "Philippians", "Phil", Testament.NEW, "Epistles", 4),
        BibleBook(51, "Colossians", "Col", Testament.NEW, "Epistles", 4),
        BibleBook(52, "1 Thessalonians", "1Thess", Testament.NEW, "Epistles", 5),
        BibleBook(53, "2 Thessalonians", "2Thess", Testament.NEW, "Epistles", 3),
        BibleBook(54, "1 Timothy", "1Tim", Testament.NEW, "Epistles", 6),
        BibleBook(55, "2 Timothy", "2Tim", Testament.NEW, "Epistles", 4),
        BibleBook(56, "Titus", "Titus", Testament.NEW, "Epistles", 3),
        BibleBook(57, "Philemon", "Phlm", Testament.NEW, "Epistles", 1),
        BibleBook(58, "Hebrews", "Heb", Testament.NEW, "Epistles", 13),
        BibleBook(59, "James", "Jas", Testament.NEW, "General Epistles", 5),
        BibleBook(60, "1 Peter", "1Pet", Testament.NEW, "General Epistles", 5),
        BibleBook(61, "2 Peter", "2Pet", Testament.NEW, "General Epistles", 3),
        BibleBook(62, "1 John", "1John", Testament.NEW, "General Epistles", 5),
        BibleBook(63, "2 John", "2John", Testament.NEW, "General Epistles", 1),
        BibleBook(64, "3 John", "3John", Testament.NEW, "General Epistles", 1),
        BibleBook(65, "Jude", "Jude", Testament.NEW, "General Epistles", 1),
        BibleBook(66, "Revelation", "Rev", Testament.NEW, "Prophecy", 22)
    )

    fun getBookById(id: Int): BibleBook {
        return books.find { it.id == id } ?: books.first()
    }

    fun findBookByName(query: String): BibleBook? {
        val q = query.trim().lowercase().replace(".", "")
        return books.find {
            it.name.lowercase() == q ||
            it.shortName.lowercase() == q ||
            it.name.lowercase().startsWith(q) ||
            q.startsWith(it.shortName.lowercase())
        }
    }

    fun parsePassageReferences(input: String): List<Triple<BibleBook, Int, Int>> {
        val regex = Regex("""((?:\d\s*)?[a-zA-Z]+(?:\s+[a-zA-Z]+)?)\s*(\d+)[:\s]+(\d+)""")
        val matches = regex.findAll(input)
        val results = mutableListOf<Triple<BibleBook, Int, Int>>()

        for (match in matches) {
            val rawBook = match.groupValues[1].trim()
            val chapter = match.groupValues[2].toIntOrNull() ?: 1
            val verse = match.groupValues[3].toIntOrNull() ?: 1
            val book = findBookByName(rawBook)
            if (book != null) {
                results.add(Triple(book, chapter, verse))
            }
        }
        return results
    }
}
