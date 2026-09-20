package com.mrboomdev.boomeditor.ui

import android.R.attr.textStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.mrboomdev.boomeditor.AppState
import com.mrboomdev.boomeditor.R
import com.mrboomdev.boomeditor.canvas.layer.Layer
import com.mrboomdev.boomeditor.ui.components.PillShapedTabBar
import com.mrboomdev.boomeditor.ui.components.ToolButton
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    appState: AppState,
) {
    val coroutineScope = rememberCoroutineScope()
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val pagerState = rememberPagerState { 3 }
    var selectedLayer by remember { mutableStateOf<Layer?>(null) }
    
    val tabScrollStates = Array(3) {
        rememberScrollState()
    }

    LaunchedEffect(pagerState.currentPage) {
        coroutineScope.launch { 
            tabScrollStates[pagerState.currentPage].scrollTo(0)   
        }
    }
    
    val mainActions = remember { 
        listOf(
            MainAction(
                text = "Add",
                icon = R.drawable.add_2_24px,
                dropdown = listOf(
                    MainAction(
                        text = "Image",
                        icon = R.drawable.image_24px
                    ),

                    MainAction(
                        text = "Shape",
                        icon = R.drawable.circle_24px
                    ),

                    MainAction(
                        text = "Draw",
                        icon = R.drawable.brush_24px
                    ),

                    MainAction(
                        text = "Text",
                        icon = R.drawable.format_size_24px
                    )
                )
            ),

            MainAction(
                text = "Grid",
                icon = R.drawable.grid_3x3_24px
            ),

            MainAction(
                text = "Layers",
                icon = R.drawable.layers_24px
            ),

            MainAction(
                text = "More",
                icon = R.drawable.more_vert_24px
            )
        )
    }
    
    val toolActions = remember { 
        listOf(
            R.drawable.crop_24px to "Crop",
            R.drawable.recenter_24px to "Position",
            R.drawable.cached_24px to "Rotate",
            R.drawable.arrows_outward_24px to "Scale",
            R.drawable.colors_24px to "Color",
            R.drawable.texture_24px to "Texture",
            R.drawable.opacity_24px to "Opacity"
        )
    }
    
    Box(Modifier.fillMaxSize()) {
        Row {
            if(windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Left + WindowInsetsSides.Vertical
                        ).asPaddingValues())
                        .fillMaxHeight()
                ) {
                    for(action in mainActions) {
                        TooltipBox(
                            state = rememberTooltipState(),
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Right),
                            tooltip = {
                                PlainTooltip {
                                    Text(action.text)
                                }
                            }
                        ) {
                            TextButton(
                                shape = RoundedCornerShape(8.dp),
                                onClick = {
                                    if(action.action != null) {
                                        action.action()
                                        return@TextButton
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(action.icon),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }

            if(!windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                            ).asPaddingValues())
                            .fillMaxWidth()
                    ) {
                        for(action in mainActions) {
                            Box(Modifier.weight(1f)) {
                                TooltipBox(
                                    state = rememberTooltipState(),
                                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Right),
                                    tooltip = {
                                        PlainTooltip {
                                            Text(action.text)
                                        }
                                    }
                                ) {
                                    TextButton(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        onClick = {
                                            if(action.action != null) {
                                                action.action()
                                                return@TextButton
                                            }
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(action.icon),
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
                            ).asPaddingValues())
                            .fillMaxWidth()
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            userScrollEnabled = false
                        ) { currentTab ->
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(
                                    items = toolActions,
                                    key = { it.second }
                                ) { tool ->
                                    ToolButton(
                                        icon = painterResource(tool.first),
                                        title = tool.second,
                                        onClick = {}
                                    )
                                }
                            }
                        }

                        PillShapedTabBar(
                            modifier = Modifier
                                .height(32.dp)
                                .padding(horizontal = 8.dp),
                            containerColor = Color.Transparent,
                            selectedIndex = pagerState.currentPage,

                            onTabSelected = { index ->
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },

                            items = remember {
                                listOf(
                                    "Transform",
                                    "Edit",
                                    "Filters"
                                )
                            }
                        )
                    }
                }   
            } else {
                Box(Modifier.weight(1f)) {}
            }
            
            if(windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Right + WindowInsetsSides.Vertical
                        ).asPaddingValues())
                        .fillMaxHeight()
                        .width(200.dp)
                ) {
                    HorizontalPager(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        state = pagerState,
                        userScrollEnabled = true
                    ) { currentTab ->
                        LazyVerticalGrid(
                            modifier = Modifier.fillMaxHeight(),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            columns = GridCells.Adaptive(48.dp),
                        ) {
                            items(
                                items = toolActions,
                                key = { it.second }
                            ) { tool ->
                                ToolButton(
                                    icon = painterResource(tool.first),
                                    title = tool.second,
                                    onClick = {}
                                )
                            }
                        }
                    }

                    PillShapedTabBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .padding(horizontal = 8.dp),
                        containerColor = Color.Transparent,
                        selectedIndex = pagerState.currentPage,
                        textStyle = MaterialTheme.typography.labelSmall,

                        onTabSelected = { index ->
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },

                        items = remember {
                            listOf(
                                "Transform",
                                "Edit",
                                "Filters"
                            )
                        }
                    )
                }   
            }
        }
    }
}

@Preview
@Preview(device = "id:pixel_tablet")
@Composable
private fun AppScreenPreview() {
    val appState = remember { AppState() }
    
    BoomEditorTheme {
        AppScreen(appState)
    }
}