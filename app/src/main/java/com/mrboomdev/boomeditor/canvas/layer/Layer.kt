package com.mrboomdev.boomeditor.canvas.layer

import androidx.compose.ui.graphics.drawscope.DrawScope

abstract class Layer(
    var x: Float,
    var y: Float,
    var rotate: Float,
    var width: Float,
    var height: Float
) {
    abstract fun draw(drawScope: DrawScope)
}