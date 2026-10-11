package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.HighlightEntity
import com.example.model.HighlightColor
import com.example.model.ReaderFont
import com.example.model.ReaderSettings
import com.example.model.ReadingTheme
import com.example.model.Translation
import com.example.model.Verse
import com.example.ui.BibleViewModel
import com.example.ui.components.BookChapterVersePickerSheet
import com.example.ui.components.CompareTranslationsSheet
import com.example.ui.components.ReaderSettingsSheet
import com.example.ui.components.SaveToCollectionDialog
import com.example.ui.components.TranslationSelectorSheet
import com.example.ui.components.VerseActionBar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    viewModel: BibleViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val currentBook by viewModel.currentBook.collectAsStateWithLifecycle()
    val currentChapter by viewModel.currentChapter.collectAsStateWithLifecycle()
    val currentTranslation by viewModel.currentTranslation.collectAsStateWithLifecycle()
    val verses by viewModel.versesInChapter.collectAsStateWithLifecycle()
    val selectedVerses by viewModel.selectedVerses.collectAsStateWithLifecycle()
    val highlightsMap by viewModel.highlightsInChapter.collectAsStateWithLifecycle()
    val readerSettings by viewModel.readerSettings.collectAsStateWithLifecycle()
    val collections by viewModel.collections.collectAsStateWithLifecycle()
    val targetVerse by viewModel.targetVerseToScroll.collectAsStateWithLifecycle()

    var showBookPicker by remember { mutableStateOf(false) }
    var showTranslationPicker by remember { mutableStateOf(false) }
    var showReaderSettings by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showCompareDialog by remember { mutableStateOf(false) }

    // TextToSpeech support
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isSpeaking by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        var textToSpeech: TextToSpeech? = null
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.US
            }
        }
        tts = textToSpeech

        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    val listState = rememberLazyListState()

    var previousIndex by remember { mutableIntStateOf(0) }
    var previousScrollOffset by remember { mutableIntStateOf(0) }

    LaunchedEffect(listState) {
        snapshotFlow {
            Pair(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset)
        }.collect { (currentIndex, currentOffset) ->
            if (currentIndex == 0 && currentOffset <= 15) {
                // At very top -> return pills
                viewModel.setScrolledDown(false)
            } else if (currentIndex > previousIndex || (currentIndex == previousIndex && currentOffset > previousScrollOffset + 15)) {
                // Scrolling DOWN -> combine into rectangle flush with bottom
                viewModel.setScrolledDown(true)
            } else if (currentIndex < previousIndex || (currentIndex == previousIndex && currentOffset < previousScrollOffset - 15)) {
                // Scrolling UP -> return pills immediately!
                viewModel.setScrolledDown(false)
            }
            previousIndex = currentIndex
            previousScrollOffset = currentOffset
        }
    }

    // Scroll to target verse if requested
    LaunchedEffect(targetVerse, verses) {
        targetVerse?.let { vNum ->
            val index = verses.indexOfFirst { it.verse == vNum }
            if (index >= 0) {
                listState.animateScrollToItem(index)
                viewModel.onVerseScrolledTo()
            }
        }
    }

    // Scroll to top when chapter changes
    LaunchedEffect(currentBook, currentChapter) {
        if (targetVerse == null) {
            listState.scrollToItem(0)
        }
    }

    val currentTheme = readerSettings.readingTheme
    val bgColor = Color(currentTheme.bgHex)
    val textColor = Color(currentTheme.textHex)

    val fontFamily = when (readerSettings.fontStyle) {
        ReaderFont.SERIF -> FontFamily.Serif
        ReaderFont.SANS -> FontFamily.SansSerif
        ReaderFont.MONO -> FontFamily.Monospace
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Elegant Top Reading Header
            Surface(
                color = Color(currentTheme.cardHex).copy(alpha = 0.95f),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Book & Chapter Picker Pill Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { showBookPicker = true }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("book_chapter_picker_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Book,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${currentBook.name} $currentChapter",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Select Book or Chapter",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Translation Pill Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .clickable { showTranslationPicker = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("translation_picker_button")
                        ) {
                            Text(
                                text = currentTranslation.code,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        // TextToSpeech Read Aloud Toggle
                        IconButton(
                            onClick = {
                                if (isSpeaking) {
                                    tts?.stop()
                                    isSpeaking = false
                                } else {
                                    val textToRead = "${currentBook.name} chapter $currentChapter. " +
                                        verses.joinToString(". ") { it.text }
                                    tts?.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, "read_chapter")
                                    isSpeaking = true
                                    Toast.makeText(context, "Reading ${currentBook.name} $currentChapter aloud", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.testTag("tts_button")
                        ) {
                            Icon(
                                if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Read chapter aloud",
                                tint = if (isSpeaking) MaterialTheme.colorScheme.error else textColor
                            )
                        }

                        // Appearance Settings
                        IconButton(
                            onClick = { showReaderSettings = true },
                            modifier = Modifier.testTag("reader_settings_button")
                        ) {
                            Icon(
                                Icons.Default.FormatSize,
                                contentDescription = "Adjust reader appearance",
                                tint = textColor
                            )
                        }
                    }
                }
            }

            // Scripture Verses LazyColumn
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .testTag("verses_column"),
                contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
            ) {
                // Chapter Header
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentBook.name.uppercase(),
                            fontSize = (readerSettings.fontSizeSp + 8).sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = fontFamily,
                            color = textColor
                        )
                        Text(
                            text = "CHAPTER $currentChapter",
                            fontSize = (readerSettings.fontSizeSp - 2).sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor.copy(alpha = 0.7f),
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentTranslation.title,
                            fontSize = 12.sp,
                            color = textColor.copy(alpha = 0.5f)
                        )
                    }
                }

                // Verses
                itemsIndexed(verses, key = { _, v -> "${v.bookId}:${v.chapter}:${v.verse}" }) { _, verse ->
                    val isSelected = selectedVerses.contains(verse.verse)
                    val highlight = highlightsMap[verse.verse]

                    val highlightBgColor = remember(highlight) {
                        highlight?.let { h ->
                            runCatching {
                                Color(android.graphics.Color.parseColor(h.colorHex)).copy(alpha = 0.35f)
                            }.getOrNull()
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                    highlightBgColor != null -> highlightBgColor
                                    else -> Color.Transparent
                                }
                            )
                            .clickable {
                                viewModel.toggleVerseSelection(verse.verse)
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("verse_item_${verse.verse}")
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            if (readerSettings.showVerseNumbers) {
                                Text(
                                    text = "${verse.verse} ",
                                    fontSize = (readerSettings.fontSizeSp - 4).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary
                                    else textColor.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(top = 2.dp, end = 4.dp)
                                )
                            }

                            val isChristWords = readerSettings.wordsOfChristRed && verse.isWordsOfChrist
                            val verseTextColor = if (isChristWords) {
                                Color(0xFFC62828) // Crimson red for Words of Christ
                            } else {
                                textColor
                            }

                            Text(
                                text = verse.text,
                                fontSize = readerSettings.fontSizeSp.sp,
                                lineHeight = (readerSettings.fontSizeSp * readerSettings.lineSpacingMultiplier).sp,
                                fontFamily = fontFamily,
                                color = verseTextColor,
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                            )
                        }
                    }
                }

                // Chapter Navigation Footer (Prev / Next Chapter)
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.previousChapter() },
                            modifier = Modifier.testTag("prev_chapter_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Previous")
                        }

                        Text(
                            text = "${currentBook.shortName} $currentChapter",
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.6f)
                        )

                        ElevatedButton(
                            onClick = { viewModel.nextChapter() },
                            modifier = Modifier.testTag("next_chapter_button")
                        ) {
                            Text("Next")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Floating Verse Action Bar (docked when verses are selected)
        VerseActionBar(
            selectedCount = selectedVerses.size,
            referenceLabel = run {
                val sorted = selectedVerses.sorted()
                if (sorted.isEmpty()) ""
                else if (sorted.size == 1) "${currentBook.name} $currentChapter:${sorted.first()} (${currentTranslation.code})"
                else "${currentBook.name} $currentChapter:${sorted.first()}-${sorted.last()} (${currentTranslation.code})"
            },
            onColorSelected = { color ->
                viewModel.applyHighlightToSelected(color)
                Toast.makeText(context, "Highlighted with ${color.name}", Toast.LENGTH_SHORT).show()
            },
            onRemoveHighlight = {
                viewModel.removeHighlightFromSelected()
                Toast.makeText(context, "Highlight removed", Toast.LENGTH_SHORT).show()
            },
            onCompare = {
                showCompareDialog = true
            },
            onShare = {
                val text = viewModel.getFormattedSelectedText()
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, text)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "Share Scripture via Biblios")
                context.startActivity(shareIntent)
            },
            onCopy = {
                val text = viewModel.getFormattedSelectedText()
                clipboardManager.setText(AnnotatedString(text))
                Toast.makeText(context, "Passage copied to clipboard", Toast.LENGTH_SHORT).show()
                viewModel.clearVerseSelection()
            },
            onSaveToCollection = {
                showSaveDialog = true
            },
            onClearSelection = {
                viewModel.clearVerseSelection()
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Modal Sheets
    if (showBookPicker) {
        BookChapterVersePickerSheet(
            allBooks = viewModel.allBooks,
            currentBook = currentBook,
            currentChapter = currentChapter,
            onDismiss = { showBookPicker = false },
            onSelectionComplete = { book, ch, vNum ->
                viewModel.selectBookAndChapter(book, ch, vNum)
            }
        )
    }

    if (showTranslationPicker) {
        TranslationSelectorSheet(
            currentTranslation = currentTranslation,
            onSelectTranslation = { tr ->
                viewModel.setTranslation(tr)
                Toast.makeText(context, "Switched to ${tr.title}", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showTranslationPicker = false }
        )
    }

    if (showReaderSettings) {
        ReaderSettingsSheet(
            settings = readerSettings,
            onSettingsChanged = { newSettings ->
                viewModel.updateReaderSettings { newSettings }
            },
            onDismiss = { showReaderSettings = false }
        )
    }

    if (showSaveDialog) {
        val refLabel = run {
            val sorted = selectedVerses.sorted()
            if (sorted.isEmpty()) ""
            else if (sorted.size == 1) "${currentBook.name} $currentChapter:${sorted.first()} (${currentTranslation.code})"
            else "${currentBook.name} $currentChapter:${sorted.first()}-${sorted.last()} (${currentTranslation.code})"
        }

        SaveToCollectionDialog(
            collections = collections,
            referenceLabel = refLabel,
            onSaveToExisting = { colId, note ->
                viewModel.saveSelectedToCollection(colId, note)
                showSaveDialog = false
                Toast.makeText(context, "Saved to collection", Toast.LENGTH_SHORT).show()
            },
            onCreateAndSave = { name, note, colorHex ->
                viewModel.createCollection(name, "", colorHex, "bookmark")
                // Save to newest collection
                viewModel.saveSelectedToCollection(collections.firstOrNull()?.id ?: 1L, note)
                showSaveDialog = false
                Toast.makeText(context, "Created collection '$name' and saved passage", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showSaveDialog = false }
        )
    }

    if (showCompareDialog) {
        val refLabel = run {
            val sorted = selectedVerses.sorted()
            if (sorted.isEmpty()) ""
            else if (sorted.size == 1) "${currentBook.name} $currentChapter:${sorted.first()}"
            else "${currentBook.name} $currentChapter:${sorted.first()}-${sorted.last()}"
        }
        val comparisonMap = viewModel.getVersesForComparison(selectedVerses)

        CompareTranslationsSheet(
            referenceLabel = refLabel,
            comparisonMap = comparisonMap,
            activeTranslation = currentTranslation,
            onSelectTranslationToRead = { newTr ->
                viewModel.setTranslation(newTr)
                showCompareDialog = false
                Toast.makeText(context, "Switched reading to ${newTr.title}", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showCompareDialog = false }
        )
    }
}
