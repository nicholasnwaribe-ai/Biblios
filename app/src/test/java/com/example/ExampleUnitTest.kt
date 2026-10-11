package com.example

import com.example.data.BibleCanon
import com.example.data.BibleScriptureData
import com.example.model.Translation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun `test bible canon has 66 books`() {
        assertEquals(66, BibleCanon.books.size)
        assertEquals(39, BibleCanon.books.count { it.testament == com.example.model.Testament.OLD })
        assertEquals(27, BibleCanon.books.count { it.testament == com.example.model.Testament.NEW })
    }

    @Test
    fun `test translations are available and load verses`() {
        val translations = listOf(Translation.NIV, Translation.NKJV, Translation.KJV, Translation.GNT)
        for (tr in translations) {
            val genesisVerses = BibleScriptureData.getVerses(1, 1, tr)
            assertTrue("Verses should not be empty for $tr", genesisVerses.isNotEmpty())
            assertTrue(genesisVerses.first().text.contains("beginning", ignoreCase = true))

            val psalm23 = BibleScriptureData.getVerses(19, 23, tr)
            assertTrue("Psalm 23 should not be empty for $tr", psalm23.isNotEmpty())
            assertTrue(psalm23.first().text.contains("shepherd", ignoreCase = true))
        }
    }
}
