package com.mrboomdev.boomeditor.canvas

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.input.pointer.pointerInput
import com.mrboomdev.boomeditor.EditorState
import com.mrboomdev.boomeditor.canvas.layer.Layer

@Composable
fun EditorCanvas(state: EditorState) {
    // Animated phase for the marching-ants border
    val infiniteTransition = rememberInfiniteTransition(label = "selectionBorder")
    val borderPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 64f, // one full dash+gap cycle
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2_500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "borderPhase"
    )

    Canvas(
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val canvasX = down.position.x - (state.maxUiInsets.left.floatValue + 50)
                    val canvasY = down.position.y - (state.maxUiInsets.top.floatValue + 50)

                    // Select layer under the finger (top-most first)
                    state.selectLayerAt(canvasX, canvasY)

                    val layer = state.selectedLayer ?: return@awaitEachGesture

                    // Only drag if the finger is actually on the selected layer
                    if (!layer.containsPoint(canvasX, canvasY)) return@awaitEachGesture

                    // Save initial state for computing deltas
                    val downPosX = down.position.x
                    val downPosY = down.position.y
                    var initLayerX = layer.x
                    var initLayerY = layer.y

                    // Multi-touch transform state
                    var lastTouchCount = 1
                    var isTransforming = false
                    var prevSpan = 0f
                    var prevAngle = 0f
                    var baseRotation = layer.rotate

                    down.consume()

                    while (true) {
                        val event = awaitPointerEvent()

                        // All fingers lifted → done
                        if (event.changes.all { !it.pressed }) break

                        val active = event.changes.filter { it.pressed }

                        // Transition to two-finger mode
                        if (active.size >= 2 && lastTouchCount < 2) {
                            isTransforming = true
                            val p0 = active[0].position
                            val p1 = active[1].position
                            prevSpan = Offset(p0.x - p1.x, p0.y - p1.y).getDistance()
                            prevAngle = angleBetween(p0, p1)
                            baseRotation = layer.rotate
                            // Reset base position so single-finger offset doesn't leak
                            initLayerX = layer.x
                            initLayerY = layer.y
                            downPosX.also { /* keep */ }
                        }

                        if (isTransforming && active.size >= 2) {
                            val p0 = active[0].position
                            val p1 = active[1].position
                            val span = Offset(p0.x - p1.x, p0.y - p1.y).getDistance()
                            val angle = angleBetween(p0, p1)

                            if (prevSpan > 0f) {
                                val scale = span / prevSpan
                                layer.width = (layer.width * scale).coerceIn(20f, 4000f)
                                layer.height = (layer.height * scale).coerceIn(20f, 4000f)
                            }
                            layer.rotate = baseRotation +
                                    Math.toDegrees((angle - prevAngle).toDouble()).toFloat()

                            prevSpan = span
                            prevAngle = angle
                        } else if (!isTransforming && active.isNotEmpty()) {
                            // Single-finger drag — always relative to original down position
                            val pointer = active.first()
                            layer.x = initLayerX + (pointer.position.x - downPosX)
                            layer.y = initLayerY + (pointer.position.y - downPosY)
                        }

                        lastTouchCount = active.size
                        active.forEach { it.consume() }
                    }
                }
            }
    ) {
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

            // Draw layers
            for (layer in state.layers) {
                layer.draw(this)
            }

            // Draw animated marching-ants selection border
            val selected = state.selectedLayer
            if (selected != null && selected in state.layers) {
                drawMarchingAnts(selected, borderPhase)
            }
        }
    }
}

private fun angleBetween(p0: Offset, p1: Offset): Float =
    kotlin.math.atan2(
        (p1.y - p0.y).toDouble(),
        (p1.x - p0.x).toDouble()
    ).toFloat()

private fun DrawScope.drawMarchingAnts(layer: Layer, phase: Float) {
    val pad = 4f
    val left = layer.x - pad
    val top = layer.y - pad
    val right = layer.x + layer.width + pad
    val bottom = layer.y + layer.height + pad

    val dash = 10f
    val gap = 6f
    val strokeW = 4f

    val rectPath = androidx.compose.ui.graphics.Path().apply {
        moveTo(left, top)
        lineTo(right, top)
        lineTo(right, bottom)
        lineTo(left, bottom)
        close()
    }

    // Black dashes
    drawPath(
        path = rectPath,
        color = Color.Black,
        style = Stroke(
            width = strokeW,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(dash, gap),
                phase = phase
            )
        )
    )

    // Gray dashes — shifted by one full cycle so they fill the gaps
    drawPath(
        path = rectPath,
        color = Color(0xFF808080),
        style = Stroke(
            width = strokeW,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(dash, gap),
                phase = phase + dash + gap
            )
        )
    )
}
