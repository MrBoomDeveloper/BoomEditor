package com.mrboomdev.boomeditor

import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import com.mrboomdev.boomeditor.canvas.layer.Layer

class EditorState {
    val maxUiInsets: EditorUiInsets = EditorUiInsets()
    val canvasWidth = mutableIntStateOf(1280)
    val canvasHeight = mutableIntStateOf(720)
//    val actions = mutableStateListOf<CanvasAction>()
    val layers = mutableStateListOf<Layer>()
    
    class EditorUiInsets(
        val right: MutableFloatState = mutableFloatStateOf(0f),
        val top: MutableFloatState = mutableFloatStateOf(0f),
        val left: MutableFloatState = mutableFloatStateOf(0f),
        val bottom: MutableFloatState = mutableFloatStateOf(0f)
    )
}