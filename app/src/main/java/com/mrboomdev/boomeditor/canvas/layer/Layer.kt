package com.mrboomdev.boomeditor.canvas.layer

import androidx.compose.ui.graphics.drawscope.DrawScope

abstract class Layer(
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float,
    var rotate: Float = 0f,
    var visible: Boolean = true
) {
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