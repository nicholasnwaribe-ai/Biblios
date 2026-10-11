package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BibleScriptureData
import com.example.model.BibleBook
import com.example.model.Testament

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookChapterVersePickerSheet(
    allBooks: List<BibleBook>,
    currentBook: BibleBook,
    currentChapter: Int,
    onDismiss: () -> Unit,
    onSelectionComplete: (book: BibleBook, chapter: Int, verse: Int?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Expand current book initially for instant chapter access
    var expandedBookId by remember { mutableIntStateOf(currentBook.id) }

    // State for popup window from under for verse selection
    var activeChapterForVersePicker by remember { mutableStateOf<Pair<BibleBook, Int>?>(null) }

    var testamentFilter by remember { mutableStateOf<Testament?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredBooks = remember(allBooks, testamentFilter, searchQuery) {
        allBooks.filter { book ->
            val matchesTestament = testamentFilter == null || book.testament == testamentFilter
            val matchesQuery = searchQuery.isBlank() ||
                book.name.contains(searchQuery, ignoreCase = true) ||
                book.shortName.contains(searchQuery, ignoreCase = true)
            matchesTestament && matchesQuery
        }
    }

    val listState = rememberLazyListState()

    // Scroll to active book on open
    LaunchedEffect(Unit) {
        val index = filteredBooks.indexOfFirst { it.id == currentBook.id }
        if (index >= 0) {
            listState.scrollToItem(index)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.testTag("book_chapter_picker_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 20.dp)
        ) {
            // Header - Clean and minimal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Books",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("picker_close_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Minimalist Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("picker_search_input"),
                placeholder = { Text("Search...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", modifier = Modifier.size(18.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Clean Testament Filter Chips (No numbers/counts)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = testamentFilter == null,
                    onClick = { testamentFilter = null },
                    label = { Text("All") },
                    modifier = Modifier.testTag("filter_chip_all")
                )
                FilterChip(
                    selected = testamentFilter == Testament.OLD,
                    onClick = { testamentFilter = Testament.OLD },
                    label = { Text("Old Testament") },
                    modifier = Modifier.testTag("filter_chip_ot")
                )
                FilterChip(
                    selected = testamentFilter == Testament.NEW,
                    onClick = { testamentFilter = Testament.NEW },
                    label = { Text("New Testament") },
                    modifier = Modifier.testTag("filter_chip_nt")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Clean Books List with Inline Chapters
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .testTag("unified_books_column")
            ) {
                items(filteredBooks, key = { it.id }) { book ->
                    val isExpanded = expandedBookId == book.id
                    val isCurrentBook = currentBook.id == book.id

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isExpanded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            )
                            .animateContentSize(animationSpec = tween(durationMillis = 180))
                    ) {
                        // Book Header Row - Clean and uncluttered
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedBookId = if (isExpanded) -1 else book.id
                                }
                                .padding(horizontal = 16.dp, vertical = 13.dp)
                                .testTag("book_item_${book.shortName}"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = book.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = if (isExpanded || isCurrentBook) FontWeight.Bold else FontWeight.Medium
                                )
                                if (isCurrentBook) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Current",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }

                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Inline Chapters Grid - Clean numbers without repetitive headers
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                val chapters = remember(book.totalChapters) { (1..book.totalChapters).toList() }

                                FlowRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("chapter_flow_row_${book.shortName}"),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    chapters.forEach { ch ->
                                        val isCurrentActive = isCurrentBook && ch == currentChapter

                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    if (isCurrentActive) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.surfaceVariant
                                                )
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = ripple(bounded = true)
                                                ) {
                                                    // Immediately trigger verse popup window from under!
                                                    activeChapterForVersePicker = Pair(book, ch)
                                                }
                                                .testTag("chapter_btn_${book.shortName}_$ch"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$ch",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = if (isCurrentActive) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isCurrentActive) MaterialTheme.colorScheme.onPrimary
                                                else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // VERSE POPUP WINDOW - Clean and intuitive
    activeChapterForVersePicker?.let { (book, chapter) ->
        val verseSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val totalVerses = remember(book.id, chapter) {
            BibleScriptureData.getVerseCount(book.id, chapter)
        }

        ModalBottomSheet(
            onDismissRequest = { activeChapterForVersePicker = null },
            sheetState = verseSheetState,
            modifier = Modifier.testTag("verse_popup_window")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${book.name} $chapter",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    IconButton(
                        onClick = { activeChapterForVersePicker = null },
                        modifier = Modifier.testTag("close_verse_popup")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Clean "Read Chapter" primary button
                Button(
                    onClick = {
                        onSelectionComplete(book, chapter, 1)
                        activeChapterForVersePicker = null
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("read_from_verse_1_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Read Chapter $chapter")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fast Grid of Verses
                val verseList = remember(totalVerses) { (1..totalVerses).toList() }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 48.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 260.dp)
                        .testTag("verse_grid_popup"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(verseList) { vNum ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true)
                                ) {
                                    onSelectionComplete(book, chapter, vNum)
                                    activeChapterForVersePicker = null
                                    onDismiss()
                                }
                                .testTag("verse_item_btn_$vNum"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$vNum",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
