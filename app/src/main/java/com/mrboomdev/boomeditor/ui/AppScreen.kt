package com.mrboomdev.boomeditor.ui

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.createBitmap
import androidx.window.core.layout.WindowSizeClass
import com.mrboomdev.boomeditor.EditorState
import com.mrboomdev.boomeditor.R
import com.mrboomdev.boomeditor.canvas.EditorCanvas
import com.mrboomdev.boomeditor.canvas.layer.ImageLayer
import com.mrboomdev.boomeditor.ui.components.PillShapedTabBar
import com.mrboomdev.boomeditor.ui.components.ToolButton
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    editorState: EditorState,
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val context = LocalContext.current
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val pagerState = rememberPagerState { 3 }
    var showLayersPanel by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    val tabScrollStates = Array(3) {
        rememberScrollState()
    }

    LaunchedEffect(pagerState.currentPage) {
        coroutineScope.launch { 
            tabScrollStates[pagerState.currentPage].scrollTo(0)   
        }
    }
    
    val mainActions = remember(
        showLayersPanel
    ) { 
        listOf(
            MainAction(
                text = "Add",
                icon = R.drawable.add_24px,
                dropdown = listOf(
                    MainAction(
                        text = "Image",
                        icon = R.drawable.image_24px,
                        action = {
                            coroutineScope.launch {
                                val imageFile = FileKit.openFilePicker(type = FileKitType.Image) ?: return@launch
                                val bitmap = imageFile.toImageBitmap()
                                val layer = ImageLayer(
                                    x = 50f,
                                    y = 50f,
                                    rotate = 0f,
                                    width = bitmap.width.toFloat(),
                                    height = bitmap.height.toFloat(),
                                    bitmap = bitmap
                                )
                                
                                editorState.layers.add(layer)
                                editorState.selectLayer(layer)
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
                toggled = showLayersPanel,
                action = {
                    showLayersPanel = !showLayersPanel
                    editorState.selectLayer(null)
                }
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
                        text = "Export",
                        icon = R.drawable.ios_share_24px,
                        action = {
                            showExportDialog = true
                        }
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
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Left + WindowInsetsSides.Vertical
                            ).asPaddingValues()
                        )
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
                                
                                colors = if(action.toggled) {
                                    ButtonDefaults.buttonColors()
                                } else ButtonDefaults.textButtonColors(),
                                
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
                    
                    Column(
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        TextButton(
                            shape = RoundedCornerShape(8.dp),
                            onClick = { editorState.zoomIn() }
                        ) { 
                            Icon(
                                painter = painterResource(R.drawable.zoom_in_24px),
                                contentDescription = null
                            )
                        }

                        TextButton(
                            shape = RoundedCornerShape(8.dp),
                            onClick = { editorState.zoomOut() }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.zoom_out_24px),
                                contentDescription = null
                            )
                        }

                        TextButton(
                            shape = RoundedCornerShape(8.dp),
                            onClick = { editorState.resetView() }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.youtube_searched_for_24px),
                                contentDescription = null
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        TextButton(
                            shape = RoundedCornerShape(8.dp),
                            enabled = false,
                            onClick = {

                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.undo_24px),
                                contentDescription = null
                            )
                        }

                        TextButton(
                            shape = RoundedCornerShape(8.dp),
                            enabled = false,
                            onClick = {

                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.redo_24px),
                                contentDescription = null
                            )
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
                            .padding(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                                ).asPaddingValues()
                            )
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
                            .padding(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
                                ).asPaddingValues()
                            )
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
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) { 
                    Row {
                        // TODO: Add here undo, redo, zoom in and zoom out buttons
                    }
                }
            }
            
            AnimatedVisibility(
                visible = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) && editorState.selectedLayer != null,
                enter = slideIn { IntOffset(it.width, 0) },
                exit = slideOut { IntOffset(it.width, 0) }
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Right + WindowInsetsSides.Vertical
                            ).asPaddingValues()
                        )
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
                            columns = GridCells.Adaptive(72.dp),
                        ) {
                            items(
                                items = toolActions,
                                key = { it.second }
                            ) { tool ->
                                ToolButton(
                                    modifier = Modifier.fillMaxWidth(),
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
        
        // Layers panel
        AnimatedVisibility(
            visible = showLayersPanel,
            enter = slideIn { IntOffset(it.width, 0) },
            exit = slideOut { IntOffset(it.width, 0) },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            LayersPanel(
                editorState = editorState,
                onDismiss = {
                    showLayersPanel = false
                    editorState.selectLayer(null)
                }
            )
        }
    }
    
    if(showExportDialog) {
        val fileTypes = remember { 
            listOf(
                ".png",
                ".jpg",
                ".webp"
            )
        }
        
        var expand by remember { mutableStateOf(false) }
        var selectedFileType by remember { mutableStateOf(fileTypes[0]) }
        var isExportInProgress by remember { mutableStateOf(false) }
        
        if(isExportInProgress) {
            Dialog(onDismissRequest = {}) {
                CircularProgressIndicator()
            }
        }

        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export image") },
            
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        text = "File type"
                    )

                    ExposedDropdownMenuBox(
                        expanded = expand,
                        onExpandedChange = { expand = it }
                    ) {
                        TextField(
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            textStyle = MaterialTheme.typography.labelLarge,
                            readOnly = true,
                            value = selectedFileType,
                            onValueChange = {}
                        )

                        ExposedDropdownMenu(
                            expanded = expand,
                            onDismissRequest = { expand = false }
                        ) {
                            for(fileType in fileTypes) {
                                DropdownMenuItem(
                                    text = { Text(fileType) },
                                    onClick = {
                                        selectedFileType = fileType
                                        expand = false
                                    }
                                )
                            }
                        }
                    }   
                    
                    // TODO: Add export quality setting
                }
            },                    confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            isExportInProgress = true
                            
                            val canvasWidth = editorState.canvasWidth.intValue
                            val canvasHeight = editorState.canvasHeight.intValue
                            val imageBitmap = createBitmap(canvasWidth, canvasHeight).asImageBitmap()
                            val canvas = androidx.compose.ui.graphics.Canvas(imageBitmap)

                            val canvasDrawScope = CanvasDrawScope()
                            val drawSize = Size(
                                canvasWidth.toFloat(),
                                canvasHeight.toFloat()
                            )

                            canvasDrawScope.draw(
                                density = density,
                                layoutDirection = layoutDirection,
                                canvas = canvas,
                                size = drawSize
                            ) {
                                // Draw all visible layers
                                for (layer in editorState.layers) {
                                    if (layer.visible) {
                                        layer.draw(this)
                                    }
                                }
                            }

                            // Compress
                            val androidBitmap = imageBitmap.asAndroidBitmap()
                            
                            val (compressFormat, mimeType) = when(selectedFileType) {
                                ".jpg" -> Bitmap.CompressFormat.JPEG to "image/jpeg"
                                ".webp" -> Bitmap.CompressFormat.WEBP_LOSSLESS to "image/webp"
                                else -> Bitmap.CompressFormat.PNG to "image/png"
                            }

                            val outputStream = java.io.ByteArrayOutputStream()
                            androidBitmap.compress(compressFormat, 100, outputStream)
                            val imageBytes = outputStream.toByteArray()

                            // Save via MediaStore
                            val resolver = context.contentResolver
                            val contentValues = android.content.ContentValues().apply {
                                put(
                                    android.provider.MediaStore.MediaColumns.DISPLAY_NAME,
                                    "BoomEditor_${System.currentTimeMillis()}${selectedFileType}"
                                )
                                
                                put(
                                    android.provider.MediaStore.MediaColumns.MIME_TYPE,
                                    mimeType
                                )
                                
                                put(
                                    android.provider.MediaStore.MediaColumns.RELATIVE_PATH,
                                    android.os.Environment.DIRECTORY_PICTURES
                                )
                            }
                            
                            val imageUri = resolver.insert(
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                contentValues
                            )
                            
                            if (imageUri != null) {
                                resolver.openOutputStream(imageUri)?.use { stream ->
                                    stream.write(imageBytes)
                                }
                            }

                            isExportInProgress = false
                            showExportDialog = false
                        }
                    }
                ) { 
                    Text("Export")
                }
            },
            
            dismissButton = {
                TextButton(
                    onClick = {
                        showExportDialog = false
                    }
                ) { 
                    Text("Cancel")
                }
            }
        )
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

@Composable
private fun LayersPanel(
    editorState: EditorState,
    onDismiss: () -> Unit
) {
    var draggedLayerId by remember { mutableIntStateOf(-1) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    var swipeOffsetX by remember { mutableFloatStateOf(0f) }
    val swipeDismissThreshold = 120f

    Row(
        modifier = Modifier
            .graphicsLayer {
                translationX = swipeOffsetX
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (swipeOffsetX > swipeDismissThreshold) {
                            onDismiss()
                        }
                        swipeOffsetX = 0f
                    },
                    onDragCancel = {
                        swipeOffsetX = 0f
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        swipeOffsetX = (swipeOffsetX + dragAmount).coerceAtLeast(0f)
                    }
                )
            }
            .clip(RoundedCornerShape(32.dp, 0.dp, 0.dp, 32.dp))
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.25f
                    )
                )
        )

        LazyColumn(
            modifier = Modifier
                .width(256.dp)
                .fillMaxHeight(),
            contentPadding = WindowInsets.safeDrawing.only(
                WindowInsetsSides.Right + WindowInsetsSides.Vertical
            ).asPaddingValues().plus(PaddingValues(
                top = 8.dp, end = 8.dp, bottom = 8.dp
            )),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            itemsIndexed(
                items = editorState.layers,
                key = { _, layer -> layer.id }
            ) { index, layer ->
                val isDragged = draggedLayerId == layer.id
                val itemHeight = 64f

                Surface(
                    shape = RoundedCornerShape(8.dp),

                    color = if(layer == editorState.selectedLayer) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else MaterialTheme.colorScheme.surface,

                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                        .then(
                            if(isDragged) {
                                Modifier.graphicsLayer {
                                    translationY = dragOffsetY
                                    scaleX = 1.03f
                                    scaleY = 1.03f
                                    alpha = 0.9f
                                }
                            } else Modifier
                        )
                        .pointerInput(layer.id) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    draggedLayerId = layer.id
                                    dragOffsetY = 0f
                                },
                                onDragEnd = {
                                    draggedLayerId = -1
                                    dragOffsetY = 0f
                                },
                                onDragCancel = {
                                    draggedLayerId = -1
                                    dragOffsetY = 0f
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    dragOffsetY += dragAmount.y

                                    val fromId = draggedLayerId
                                    if(fromId < 0) return@detectDragGesturesAfterLongPress

                                    val fromIndex = editorState.layers.indexOfFirst { it.id == fromId }
                                    if(fromIndex < 0) return@detectDragGesturesAfterLongPress

                                    val swapThreshold = itemHeight / 2f
                                    val displacement = (dragOffsetY / swapThreshold).roundToInt()
                                    val targetIndex = (fromIndex + displacement).coerceIn(0, editorState.layers.lastIndex)

                                    if(targetIndex != fromIndex) {
                                        editorState.layers.add(fromIndex, editorState.layers.removeAt(targetIndex))
                                        dragOffsetY = 0f
                                    }
                                }
                            )
                        },
                    onClick = {
                        editorState.selectLayer(layer)
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.drag_indicator_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = 0.25f
                            ),
                        )

                        LayerThumbnail(
                            layer = layer,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(48.dp)
                        )

                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            text = "Layer #${index + 1}"
                        )

                        IconButton(
                            onClick = {
                                layer.toggleVisible()
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                                contentDescription = null,
                                painter = painterResource(if(layer.visible) {
                                    R.drawable.visibility_24px
                                } else R.drawable.visibility_off_24px)
                            )
                        }

                        IconButton(
                            onClick = {
                                editorState.layers.remove(layer)
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                                painter = painterResource(R.drawable.delete_24px),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LayerThumbnail(
    layer: com.mrboomdev.boomeditor.canvas.layer.Layer,
    modifier: Modifier = Modifier
) {
    val canvasWidth = layer.width
    val canvasHeight = layer.height

    Canvas(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(4.dp))
            .clipToBounds()
    ) {
        val scale = if (canvasWidth > 0f && canvasHeight > 0f) {
            minOf(size.width / canvasWidth, size.height / canvasHeight)
        } else 0.1f

        val offsetX = (size.width - canvasWidth * scale) / 2f
        val offsetY = (size.height - canvasHeight * scale) / 2f

        drawIntoCanvas { canvas ->
            canvas.save()
            canvas.translate(offsetX, offsetY)
            scale(scale, pivot = Offset.Zero) {
                layer.draw(this)
            }
            canvas.restore()
        }
    }
}