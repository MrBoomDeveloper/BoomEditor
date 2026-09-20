package com.mrboomdev.boomeditor.canvas.layer

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

class ImageLayer(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    var bitmap: ImageBitmap,
    rotate: Float = 0f,
    visible: Boolean = true,
): Layer(
    x = x,
    y = y,
    width = width,
    height = height,
    rotate = rotate,
    visible = visible
) {
    override fun draw(drawScope: DrawScope) {
        drawScope.drawImage(
            image = bitmap,
            
            dstOffset = IntOffset(
                x = x.toInt(),
                y = y.toInt()
            ),
            
            dstSize = IntSize(
                width = width.toInt(),
                height = height.toInt()
            )
        )
    }
}