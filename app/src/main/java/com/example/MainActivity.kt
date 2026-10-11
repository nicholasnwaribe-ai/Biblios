package com.example

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.BibleViewModel
import com.example.ui.NavigationTab
import com.example.ui.screens.CollectionsScreen
import com.example.ui.screens.ReaderScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.BibliosTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BibleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // True Edge-to-Edge with completely transparent navigation and status bars
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            BibliosTheme {
                MainAppScaffold(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScaffold(viewModel: BibleViewModel) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val isScrolledDown by viewModel.isScrolledDown.collectAsStateWithLifecycle()

    // Back handling: pop back to Reader tab if currently on Collections or Search
    BackHandler(enabled = activeTab != NavigationTab.READER) {
        viewModel.setActiveTab(NavigationTab.READER)
    }

    // Full screen edge-to-edge container without colored system bars or cuts
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        // Main Screen Content: Extends fully end to end behind the floating/docked nav
        when (activeTab) {
            NavigationTab.READER -> ReaderScreen(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
            NavigationTab.COLLECTIONS -> CollectionsScreen(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
            NavigationTab.SEARCH -> SearchScreen(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Morphing Navigation Bar: Floating Pill when at top/scrolling up -> Docked Rectangle flush with bottom when scrolling down
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            MorphingNavigationBar(
                activeTab = activeTab,
                isScrolledDown = isScrolledDown && activeTab == NavigationTab.READER,
                onTabSelected = { viewModel.setActiveTab(it) }
            )
        }
    }
}

@Composable
fun MorphingNavigationBar(
    activeTab: NavigationTab,
    isScrolledDown: Boolean,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val navBarBottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // Smooth transitions between floating pill and docked rectangle
    val cornerRadiusTop by animateDpAsState(
        targetValue = if (isScrolledDown) 12.dp else 32.dp,
        animationSpec = tween(durationMillis = 220),
        label = "cornerRadiusTop"
    )
    val cornerRadiusBottom by animateDpAsState(
        targetValue = if (isScrolledDown) 0.dp else 32.dp,
        animationSpec = tween(durationMillis = 220),
        label = "cornerRadiusBottom"
    )
    val horizontalPadding by animateDpAsState(
        targetValue = if (isScrolledDown) 0.dp else 24.dp,
        animationSpec = tween(durationMillis = 220),
        label = "horizontalPadding"
    )
    // When scrolled down: 0.dp bottom margin so it is 100% flush with the bottom of the screen!
    val outerBottomMargin by animateDpAsState(
        targetValue = if (isScrolledDown) 0.dp else navBarBottomInset + 12.dp,
        animationSpec = tween(durationMillis = 220),
        label = "outerBottomMargin"
    )
    val innerBottomPadding by animateDpAsState(
        targetValue = if (isScrolledDown) max(navBarBottomInset, 8.dp) else 0.dp,
        animationSpec = tween(durationMillis = 220),
        label = "innerBottomPadding"
    )
    val elevation by animateDpAsState(
        targetValue = if (isScrolledDown) 8.dp else 6.dp,
        animationSpec = tween(durationMillis = 220),
        label = "elevation"
    )

    val shape = RoundedCornerShape(
        topStart = cornerRadiusTop,
        topEnd = cornerRadiusTop,
        bottomStart = cornerRadiusBottom,
        bottomEnd = cornerRadiusBottom
    )

    Surface(
        modifier = modifier
            .padding(horizontal = horizontalPadding)
            .padding(bottom = outerBottomMargin)
            .fillMaxWidth()
            .shadow(elevation, shape)
            .testTag("morphing_bottom_nav"),
        shape = shape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = if (isScrolledDown) 5.dp else 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = innerBottomPadding)
        ) {
            if (isScrolledDown) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                    thickness = 0.8.dp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isScrolledDown) 4.dp else 4.dp, vertical = if (isScrolledDown) 6.dp else 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MorphingNavItem(
                    selected = activeTab == NavigationTab.READER,
                    isRectangleMode = isScrolledDown,
                    onClick = { onTabSelected(NavigationTab.READER) },
                    icon = Icons.Default.MenuBook,
                    label = "Bible",
                    modifier = Modifier.weight(0.9f),
                    testTag = "nav_item_bible"
                )
                MorphingNavItem(
                    selected = activeTab == NavigationTab.COLLECTIONS,
                    isRectangleMode = isScrolledDown,
                    onClick = { onTabSelected(NavigationTab.COLLECTIONS) },
                    icon = Icons.Default.Bookmarks,
                    label = "Collections",
                    modifier = Modifier.weight(1.2f),
                    testTag = "nav_item_collections"
                )
                MorphingNavItem(
                    selected = activeTab == NavigationTab.SEARCH,
                    isRectangleMode = isScrolledDown,
                    onClick = { onTabSelected(NavigationTab.SEARCH) },
                    icon = Icons.Default.Search,
                    label = "Search",
                    modifier = Modifier.weight(0.9f),
                    testTag = "nav_item_search"
                )
            }
        }
    }
}

@Composable
private fun MorphingNavItem(
    selected: Boolean,
    isRectangleMode: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    testTag: String
) {
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    val itemShape = if (isRectangleMode) RoundedCornerShape(10.dp) else CircleShape

    Row(
        modifier = modifier
            .clip(itemShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            )
            .padding(horizontal = 4.dp, vertical = if (isRectangleMode) 10.dp else 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = if (isRectangleMode) 12.sp else 12.5.sp,
            maxLines = 1,
            softWrap = false,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}
