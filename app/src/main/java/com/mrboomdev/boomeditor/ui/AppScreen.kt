package com.mrboomdev.boomeditor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.mrboomdev.boomeditor.EditorState
import com.mrboomdev.boomeditor.R
import com.mrboomdev.boomeditor.canvas.EditorCanvas
import com.mrboomdev.boomeditor.canvas.layer.ImageLayer
import com.mrboomdev.boomeditor.canvas.layer.Layer
import com.mrboomdev.boomeditor.ui.components.PillShapedTabBar
import com.mrboomdev.boomeditor.ui.components.ToolButton
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    editorState: EditorState,
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
                        icon = R.drawable.image_24px,
                        action = {
                            coroutineScope.launch {
                                val imageFile = FileKit.openFilePicker(type = FileKitType.Image) ?: return@launch
                                val bitmap = imageFile.toImageBitmap()
                                editorState.layers.add(ImageLayer(
                                    x = 50f,
                                    y = 50f,
                                    rotate = 0f,
                                    width = bitmap.width.toFloat(),
                                    height = bitmap.height.toFloat(),
                                    bitmap = bitmap
                                ))
                            }
                        }
                    ),

                    MainAction(
                        text = "Shape",
                        icon = R.drawable.circle_24px,
                        action = {}
                    ),

                    MainAction(
                        text = "Draw",
                        icon = R.drawable.brush_24px,
                        action = {}
                    ),

                    MainAction(
                        text = "Text",
                        icon = R.drawable.format_size_24px,
                        action = {}
                    )
                )
            ),

            MainAction(
                text = "Grid",
                icon = R.drawable.grid_3x3_24px,
                action = {}
            ),

            MainAction(
                text = "Layers",
                icon = R.drawable.layers_24px,
                action = {}
            ),

            MainAction(
                text = "More",
                icon = R.drawable.more_vert_24px,
                dropdown = listOf(
                    MainAction(
                        text = "Save project",
                        icon = R.drawable.save_24px,
                        action = {}
                    ),

                    MainAction(
                        text = "Open project",
                        icon = R.drawable.folder_open_24px,
                        action = {}
                    ),

                    MainAction(
                        text = "Resize canvas",
                        icon = R.drawable.crop_24px,
                        action = {}
                    ),

                    MainAction(
                        text = "Resize canvas",
                        icon = R.drawable.ios_share_24px,
                        action = {}
                    )
                )
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
    
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        EditorCanvas(
            state = editorState
        )
        
        Row {
            if(windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Left + WindowInsetsSides.Vertical
                        ).asPaddingValues())
                        .fillMaxHeight()
                        .onGloballyPositioned {
                            editorState.maxUiInsets.left.floatValue = it.boundsInRoot().right
                        }
                ) {
                    for(action in mainActions) {
                        var open by remember { mutableStateOf(false) }
                        
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
                                    
                                    if(action.dropdown != null) {
                                        open = true
                                        return@TextButton
                                    }
                                    
                                    throw UnsupportedOperationException("Unsupported MainAction!")
                                }
                            ) {
                                Icon(
                                    painter = painterResource(action.icon),
                                    contentDescription = null
                                )
                            }

                            if(action.dropdown != null) {
                                DropdownMenu(
                                    expanded = open,
                                    onDismissRequest = { open = false },
                                    modifier = Modifier.widthIn(min = 150.dp),
                                    shape = RoundedCornerShape(32.dp)
                                ) {
                                    for(subAction in action.dropdown) {
                                        DropdownMenuItem(
                                            contentPadding = PaddingValues(
                                                horizontal = 16.dp
                                            ),

                                            text = {
                                                Text(subAction.text)
                                            },

                                            leadingIcon = {
                                                Icon(
                                                    painter = painterResource(subAction.icon),
                                                    contentDescription = null
                                                )
                                            },

                                            onClick = {
                                                open = false
                                                
                                                if(subAction.action != null) {
                                                    subAction.action()
                                                    return@DropdownMenuItem
                                                }

                                                throw UnsupportedOperationException("Unsupported sub MainAction!")
                                                
                                            }
                                        )
                                    }
                                }
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
                            .onGloballyPositioned {
                                editorState.maxUiInsets.top.floatValue = it.boundsInRoot().bottom
                            }
                    ) {
                        for(action in mainActions) {
                            var open by remember { mutableStateOf(false) }
                            
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

                                            if(action.dropdown != null) {
                                                open = true
                                                return@TextButton
                                            }

                                            throw UnsupportedOperationException("Unsupported MainAction!")
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(action.icon),
                                            contentDescription = null
                                        )
                                    }

                                    if(action.dropdown != null) {
                                        DropdownMenu(
                                            expanded = open,
                                            onDismissRequest = { open = false },
                                            modifier = Modifier.widthIn(min = 150.dp),
                                            shape = RoundedCornerShape(32.dp)
                                        ) {
                                            for(subAction in action.dropdown) {
                                                DropdownMenuItem(
                                                    contentPadding = PaddingValues(
                                                        horizontal = 16.dp
                                                    ),

                                                    text = {
                                                        Text(subAction.text)
                                                    },

                                                    leadingIcon = {
                                                        Icon(
                                                            painter = painterResource(subAction.icon),
                                                            contentDescription = null
                                                        )
                                                    },

                                                    onClick = {
                                                        open = false

                                                        if(subAction.action != null) {
                                                            subAction.action()
                                                            return@DropdownMenuItem
                                                        }

                                                        throw UnsupportedOperationException("Unsupported sub MainAction!")

                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(top = 8.dp)
                            .padding(WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
                            ).asPaddingValues())
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                val bounds = it.boundsInRoot()
                                editorState.maxUiInsets.bottom.floatValue = bounds.bottom - bounds.top
                            },
                        verticalArrangement = Arrangement.spacedBy(4.dp)
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
                        .onGloballyPositioned {
                            val bounds = it.boundsInRoot()
                            editorState.maxUiInsets.right.floatValue = bounds.right - bounds.left
                        }
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
    val editorState = remember { EditorState() }
    
    BoomEditorTheme {
        AppScreen(editorState)
    }
}