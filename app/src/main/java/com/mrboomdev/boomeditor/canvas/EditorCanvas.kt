package com.mrboomdev.boomeditor.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.inset
import com.mrboomdev.boomeditor.EditorState

@Composable
fun EditorCanvas(state: EditorState) {
    Canvas(Modifier.fillMaxSize()) {
        // Draw ui background
        drawRect(Color.Black)
        
        inset(
            left = state.maxUiInsets.left.floatValue + 50,
            top = state.maxUiInsets.top.floatValue + 50,
            right = state.maxUiInsets.right.floatValue + 50,
            bottom = state.maxUiInsets.bottom.floatValue + 50
        ) {
            // Draw canvas background
            drawRect(
                color = Color.White,
                size = Size(state.canvasWidth.intValue.toFloat(), state.canvasHeight.intValue.toFloat())
            )

            // Draw content
            for(layer in state.layers) {
                layer.draw(this)
            }   
        }
    }
}