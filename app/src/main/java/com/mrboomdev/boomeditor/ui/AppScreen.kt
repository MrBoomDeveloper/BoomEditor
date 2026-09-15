package com.mrboomdev.boomeditor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrboomdev.boomeditor.AppState
import com.mrboomdev.boomeditor.R
import com.mrboomdev.boomeditor.canvas.layer.Layer
import com.mrboomdev.boomeditor.ui.components.PillShapedTabBar
import com.mrboomdev.boomeditor.ui.components.ToolButton
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    appState: AppState,
) {
    val coroutineScope = rememberCoroutineScope()
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
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},

                actions = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            var showOptions by remember { mutableStateOf(false) }

                            IconButton(
                                onClick = {
                                    showOptions = true
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.add_2_24px),
                                    contentDescription = null
                                )
                            }

                            DropdownMenu(
                                modifier = Modifier.widthIn(min = 150.dp),
                                expanded = showOptions,
                                onDismissRequest = { showOptions = false },
                                shape = RoundedCornerShape(32.dp)
                            ) {
                                DropdownMenuItem(
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp
                                    ),

                                    text = {
                                        Text("Text")
                                    },

                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.format_size_24px),
                                            contentDescription = null
                                        )
                                    },

                                    onClick = {

                                    }
                                )

                                DropdownMenuItem(
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp
                                    ),

                                    text = {
                                        Text("Image")
                                    },

                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.image_24px),
                                            contentDescription = null
                                        )
                                    },

                                    onClick = {

                                    }
                                )

                                DropdownMenuItem(
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp
                                    ),

                                    text = {
                                        Text("Shape")
                                    },

                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.circle_24px),
                                            contentDescription = null
                                        )
                                    },

                                    onClick = {

                                    }
                                )

                                DropdownMenuItem(
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp
                                    ),

                                    text = {
                                        Text("Draw")
                                    },

                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.brush_24px),
                                            contentDescription = null
                                        )
                                    },

                                    onClick = {

                                    }
                                )
                            }
                        }

                        IconButton(
                            onClick = {

                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.grid_3x3_24px),
                                contentDescription = null
                            )
                        }

                        IconButton(
                            onClick = {

                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.layers_24px),
                                contentDescription = null
                            )
                        }

                        Box {
                            var showOptions by remember { mutableStateOf(false) }
                            
                            IconButton(
                                onClick = {
                                    showOptions = true
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.more_vert_24px),
                                    contentDescription = null
                                )
                            }

                            DropdownMenu(
                                modifier = Modifier.widthIn(min = 150.dp),
                                expanded = showOptions,
                                onDismissRequest = { showOptions = false },
                                shape = RoundedCornerShape(32.dp)
                            ) {
                                DropdownMenuItem(
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp
                                    ),
                                    
                                    text = {
                                        Text("Save project")
                                    },
                                    
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.save_24px),
                                            contentDescription = null
                                        )
                                    },

                                    onClick = {

                                    }
                                )

                                DropdownMenuItem(
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp
                                    ),
                                    
                                    text = {
                                        Text("Open project")
                                    },

                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.folder_open_24px),
                                            contentDescription = null
                                        )
                                    },

                                    onClick = {

                                    }
                                )
                                
                                DropdownMenuItem(
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp
                                    ),
                                    
                                    text = {
                                        Text("Resize canvas")
                                    },

                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.crop_24px),
                                            contentDescription = null
                                        )
                                    },
                                    
                                    onClick = {
                                        
                                    }
                                )

                                DropdownMenuItem(
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp
                                    ),
                                    
                                    text = {
                                        Text("Export")
                                    },

                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.ios_share_24px),
                                            contentDescription = null
                                        )
                                    },

                                    onClick = {

                                    }
                                )
                            }
                        }
                    }
                }
            )
        },

        bottomBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(top = 8.dp, bottom = 4.dp)
                    .padding(WindowInsets.navigationBars.asPaddingValues()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = false
                ) { currentTab ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(tabScrollStates[currentTab])
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ToolButton(
                            icon = painterResource(R.drawable.crop_24px),
                            title = "Crop",
                            onClick = {}
                        )

                        ToolButton(
                            icon = painterResource(R.drawable.recenter_24px),
                            title = "Position",
                            onClick = {}
                        )

                        ToolButton(
                            icon = painterResource(R.drawable.cached_24px),
                            title = "Rotate",
                            onClick = {}
                        )

                        ToolButton(
                            icon = painterResource(R.drawable.arrows_outward_24px),
                            title = "Scale",
                            onClick = {}
                        )

                        ToolButton(
                            icon = painterResource(R.drawable.colors_24px),
                            title = "Color",
                            onClick = {}
                        )

                        ToolButton(
                            icon = painterResource(R.drawable.texture_24px),
                            title = "Texture",
                            onClick = {}
                        )

                        ToolButton(
                            icon = painterResource(R.drawable.opacity_24px),
                            title = "Opacity",
                            onClick = {}
                        )
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
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0x55000000))
                ) {
                    IconButton(
                        enabled = appState.actions.isNotEmpty(),
                        onClick = {

                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.undo_24px),
                            contentDescription = null
                        )
                    }

                    IconButton(
                        enabled = appState.actions.isNotEmpty(),
                        onClick = {

                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.redo_24px),
                            contentDescription = null
                        )
                    }
                }

                if(selectedLayer != null) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(32.dp))
                            .background(Color(0x55000000))
                    ) {
                        IconButton(
                            onClick = {

                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.content_copy_24px),
                                contentDescription = null
                            )
                        }

                        IconButton(
                            onClick = {

                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.delete_24px),
                                contentDescription = null
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0x55000000))
                ) {
                    IconButton(
                        onClick = {

                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.zoom_in_24px),
                            contentDescription = null
                        )
                    }

                    IconButton(
                        onClick = {

                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.zoom_out_24px),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AppScreenPreview() {
    val appState = remember { AppState() }
    
    BoomEditorTheme {
        AppScreen(appState)
    }
}