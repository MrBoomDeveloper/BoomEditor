package com.mrboomdev.boomeditor

import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mrboomdev.boomeditor.canvas.layer.Layer

class EditorState {
    val maxUiInsets: EditorUiInsets = EditorUiInsets()
    val canvasWidth = mutableIntStateOf(1280)
    val canvasHeight = mutableIntStateOf(720)
//    val actions = mutableStateListOf<CanvasAction>()
    val layers = mutableStateListOf<Layer>()
    var selectedLayer by mutableStateOf<Layer?>(null)
        private set

    fun selectLayer(layer: Layer?) {
        selectedLayer = layer
    }

    fun selectLayerAt(x: Float, y: Float): Boolean {
        // Iterate in reverse order (top-most layer first)
        for (i in layers.indices.reversed()) {
            val layer = layers[i]
            
            if(!layer.visible) {
                continue
            }
            
            if (layer.containsPoint(x, y)) {
                selectedLayer = layers[i]
                return true
            }
        }
        
        selectedLayer = null
        return false
    }
    
    class EditorUiInsets(
        val right: MutableFloatState = mutableFloatStateOf(0f),
        val top: MutableFloatState = mutableFloatStateOf(0f),
        val left: MutableFloatState = mutableFloatStateOf(0f),
        val bottom: MutableFloatState = mutableFloatStateOf(0f)
    )
}