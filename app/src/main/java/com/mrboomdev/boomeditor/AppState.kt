package com.mrboomdev.boomeditor

import androidx.compose.runtime.mutableStateListOf
import com.mrboomdev.boomeditor.canvas.action.CanvasAction
import com.mrboomdev.boomeditor.canvas.layer.Layer

class AppState {
    val actions = mutableStateListOf<CanvasAction>()
    val layers = mutableStateListOf<Layer>()
}