package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.BibleCanon
import com.example.model.OpenedPassageItem
import com.example.ui.BibleViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    viewModel: BibleViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val currentTranslation by viewModel.currentTranslation.collectAsStateWithLifecycle()
    val openedPassages by viewModel.openedPassages.collectAsStateWithLifecycle()

    // 0 = Opened Passages Workspace, 1 = Search Results
    var selectedTab by remember { mutableIntStateOf(0) }

    // Detect if search query contains multiple verse references (e.g. "john 3:16, john 9:11")
    val parsedReferences by remember(searchQuery) {
        derivedStateOf {
            if (searchQuery.isNotBlank()) BibleCanon.parsePassageReferences(searchQuery)
            else emptyList()
        }
    }

    val sampleMultiQueries = listOf(
        "John 3:16, John 9:11",
        "Psalm 23:1, Psalm 91:1",
        "Genesis 1:1, John 1:1",
        "Romans 8:28, Phil 4:13"
    )

    Scaffold(
        modifier = modifier.testTag("search_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Search & Passages",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = if (openedPassages.size >= 20) MaterialTheme.colorScheme.errorContainer
                            else MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${openedPassages.size}/20",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (openedPassages.size >= 20) MaterialTheme.colorScheme.onErrorContainer
                                else MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    if (openedPassages.isNotEmpty()) {
                        // Share all opened passages together
                        IconButton(
                            onClick = {
                                val sb = StringBuilder()
                                sb.append("Bible Study Passages (${openedPassages.size} opened):\n\n")
                                openedPassages.forEachIndexed { idx, p ->
                                    sb.append("${idx + 1}. ${p.book.name} ${p.chapter}:${p.verse} (${p.translation.code})\n")
                                    sb.append("\"${p.text}\"\n\n")
                                }
                                val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, sb.toString().trim())
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Opened Passages"))
                            },
                            modifier = Modifier.testTag("share_all_opened_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share all opened")
                        }

                        // Copy all opened passages
                        IconButton(
                            onClick = {
                                val sb = StringBuilder()
                                sb.append("Bible Study Passages (${openedPassages.size} opened):\n\n")
                                openedPassages.forEachIndexed { idx, p ->
                                    sb.append("${idx + 1}. ${p.book.name} ${p.chapter}:${p.verse} (${p.translation.code})\n")
                                    sb.append("\"${p.text}\"\n\n")
                                }
                                clipboardManager.setText(AnnotatedString(sb.toString().trim()))
                                Toast.makeText(context, "Copied all ${openedPassages.size} passages to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("copy_all_opened_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy all")
                        }

                        // Clear all
                        IconButton(
                            onClick = {
                                viewModel.clearAllOpenedPassages()
                                Toast.makeText(context, "Cleared opened passages", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("clear_all_opened_button")
                        ) {
                            Icon(Icons.Default.ClearAll, contentDescription = "Clear all opened")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Input with Multi-Reference parser
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    viewModel.performSearch(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_text_field"),
                placeholder = { Text("Search words or e.g. John 3:16, John 9:11...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.performSearch("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            // Multi-reference detection bar: If user typed multiple passage references
            if (parsedReferences.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Found ${parsedReferences.size} scripture references",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = parsedReferences.joinToString(", ") { "${it.first.shortName} ${it.second}:${it.third}" },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        Button(
                            onClick = {
                                val count = viewModel.openMultipleReferences(searchQuery)
                                selectedTab = 0 // Switch to Opened tab
                                viewModel.performSearch("")
                                Toast.makeText(context, "Opened $count passage(s)", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("open_parsed_passages_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Open All")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Example Chips for Multi-Passage
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                sampleMultiQueries.forEach { sample ->
                    AssistChip(
                        onClick = {
                            viewModel.performSearch(sample)
                        },
                        label = { Text(sample, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Navigation Tabs: Opened Passages (X) vs Search Results
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Opened Passages (${openedPassages.size}/20)",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Search Results (${searchResults.size})",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tab 0: MULTI-PASSAGE OPENED WORKSPACE
            if (selectedTab == 0) {
                if (openedPassages.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No passages opened yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Type references like 'John 3:16, John 9:11' above or tap + on search results to open up to 20 verses at the same time.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("opened_passages_column")
                    ) {
                        itemsIndexed(openedPassages, key = { _, item -> item.id }) { index, passage ->
                            OpenedPassageCard(
                                index = index + 1,
                                passage = passage,
                                onReadInBible = {
                                    viewModel.selectBookAndChapter(
                                        book = passage.book,
                                        chapter = passage.chapter,
                                        targetVerse = passage.verse
                                    )
                                },
                                onRemove = {
                                    viewModel.removeOpenedPassage(passage.id)
                                    Toast.makeText(context, "Closed ${passage.book.name} ${passage.chapter}:${passage.verse}", Toast.LENGTH_SHORT).show()
                                },
                                onCopy = {
                                    val text = "\"${passage.text}\"\n— ${passage.book.name} ${passage.chapter}:${passage.verse} (${passage.translation.code})"
                                    clipboardManager.setText(AnnotatedString(text))
                                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                onShare = {
                                    val text = "\"${passage.text}\"\n— ${passage.book.name} ${passage.chapter}:${passage.verse} (${passage.translation.code})"
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, text)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Verse"))
                                }
                            )

                            // DISTINCT VISUAL SEPARATION BETWEEN OPENED VERSES
                            if (index < openedPassages.size - 1) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    HorizontalDivider(
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                    Surface(
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = "§",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    HorizontalDivider(
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.height(100.dp))
                            }
                        }
                    }
                }
            } else {
                // Tab 1: KEYWORD SEARCH RESULTS WITH QUICK "OPEN" ACTION
                if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isBlank()) "Type words like 'love', 'faith', or 'shepherd' to search"
                            else "No search matches for \"$searchQuery\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("search_results_list")
                    ) {
                        items(searchResults) { (book, verse) ->
                            val isAlreadyOpened = openedPassages.any { it.id == "${book.id}:${verse.chapter}:${verse.verse}" }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${book.name} ${verse.chapter}:${verse.verse}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // Quick Open Button into Workspace
                                            FilledTonalButton(
                                                onClick = {
                                                    if (!isAlreadyOpened) {
                                                        val added = viewModel.openPassage(book, verse.chapter, verse.verse)
                                                        if (added) {
                                                            Toast.makeText(context, "Opened ${book.name} ${verse.chapter}:${verse.verse}", Toast.LENGTH_SHORT).show()
                                                        } else {
                                                            Toast.makeText(context, "Maximum 20 passages already opened", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.height(32.dp)
                                            ) {
                                                Icon(
                                                    if (isAlreadyOpened) Icons.Default.Check else Icons.Default.Add,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(if (isAlreadyOpened) "Opened" else "Open", fontSize = 11.sp)
                                            }

                                            Spacer(modifier = Modifier.width(6.dp))

                                            // Open directly in Bible
                                            IconButton(
                                                onClick = {
                                                    viewModel.selectBookAndChapter(
                                                        book = book,
                                                        chapter = verse.chapter,
                                                        targetVerse = verse.verse
                                                    )
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.AutoMirrored.Filled.OpenInNew,
                                                    contentDescription = "Read in Bible",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = verse.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OpenedPassageCard(
    index: Int,
    passage: OpenedPassageItem,
    onReadInBible: () -> Unit,
    onRemove: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                shape = RoundedCornerShape(14.dp)
            )
            .testTag("opened_passage_card_${passage.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with badge, reference, and close action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Index badge
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$index",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${passage.book.name} ${passage.chapter}:${passage.verse}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = passage.translation.code,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                // Remove this opened verse
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove verse",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Verse Text
            Text(
                text = passage.text,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Actions: Read in Context, Copy, Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onReadInBible,
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Read in Bible", fontSize = 12.sp)
                }

                Row {
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
