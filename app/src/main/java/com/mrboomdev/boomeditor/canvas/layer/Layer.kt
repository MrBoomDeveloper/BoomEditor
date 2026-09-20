package com.mrboomdev.boomeditor.canvas.layer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.drawscope.DrawScope

abstract class Layer(
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float,
    var rotate: Float = 0f,
    visible: Boolean = true
) {
    val id: Int = nextId++

    var visible by mutableStateOf(visible)
        private set

    fun toggleVisible() {
        visible = !visible
    }

    companion object {
        private var nextId = 0
    }
    abstract fun draw(drawScope: DrawScope)

    /**
     * Check if a point (in canvas-local coordinates) is inside this layer.
     * Uses axis-aligned bounding box with a small touch tolerance.
     */
    open fun containsPoint(pointX: Float, pointY: Float, tolerance: Float = 0f): Boolean {
        return pointX >= x - tolerance &&
                pointX <= x + width + tolerance &&
                pointY >= y - tolerance &&
                pointY <= y + height + tolerance
    }
}