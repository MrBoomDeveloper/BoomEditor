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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import com.mrboomdev.boomeditor.EditorState
import com.mrboomdev.boomeditor.canvas.layer.Layer

private const val CORNER_HIT_SIZE = 28f
private const val MIN_LAYER_SIZE = 20f
private const val MIN_ZOOM = 0.2f
private const val MAX_ZOOM = 5f

private enum class ResizeEdge { TL, T, TR, R, BR, B, BL, L }

@Composable
fun EditorCanvas(state: EditorState) {
    val infiniteTransition = rememberInfiniteTransition(label = "selectionBorder")
    val borderPhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 64f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2_500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "borderPhase"
    )

    var panOffset by state::panOffset
    var zoom by state::zoom

    fun screenToCanvas(sx: Float, sy: Float): Offset {
        val cl = state.maxUiInsets.left.floatValue + 50
        val ct = state.maxUiInsets.top.floatValue + 50
        return Offset((sx - cl - panOffset.x) / zoom, (sy - ct - panOffset.y) / zoom)
    }

    Canvas(
        Modifier.fillMaxSize().pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val cDown = screenToCanvas(down.position.x, down.position.y)

                val sel = state.selectedLayer
                val edge = if (sel != null && sel.visible) detectResizeEdge(cDown.x, cDown.y, sel) else null

                // ─── ① RESIZE ──────────────────────────────────────
                if (edge != null && sel != null) {
                    val ix = sel.x; val iy = sel.y; val iw = sel.width; val ih = sel.height
                    val sx = down.position.x; val sy = down.position.y
                    val aspect = iw / ih
                    down.consume()
                    while (true) {
                        val ev = awaitPointerEvent(PointerEventPass.Main)
                        if (ev.changes.all { !it.pressed }) break
                        val p = ev.changes.first { it.pressed }
                        val dx = screenToCanvas(p.position.x, p.position.y).x -
                                screenToCanvas(sx, sy).x
                        val dy = screenToCanvas(p.position.x, p.position.y).y -
                                screenToCanvas(sx, sy).y
                        when (edge) {
                            // ── Corners: aspect-ratio preserving ──
                            ResizeEdge.TL -> {
                                var nw = (iw - dx).coerceAtLeast(MIN_LAYER_SIZE)
                                var nh = (ih - dy).coerceAtLeast(MIN_LAYER_SIZE)
                                if (nw / nh > aspect) nw = nh * aspect else nh = nw / aspect
                                sel.x = ix + iw - nw; sel.y = iy + ih - nh
                                sel.width = nw; sel.height = nh
                            }
                            ResizeEdge.TR -> {
                                var nw = (iw + dx).coerceAtLeast(MIN_LAYER_SIZE)
                                var nh = (ih - dy).coerceAtLeast(MIN_LAYER_SIZE)
                                if (nw / nh > aspect) nw = nh * aspect else nh = nw / aspect
                                sel.y = iy + ih - nh
                                sel.width = nw; sel.height = nh
                            }
                            ResizeEdge.BL -> {
                                var nw = (iw - dx).coerceAtLeast(MIN_LAYER_SIZE)
                                var nh = (ih + dy).coerceAtLeast(MIN_LAYER_SIZE)
                                if (nw / nh > aspect) nw = nh * aspect else nh = nw / aspect
                                sel.x = ix + iw - nw
                                sel.width = nw; sel.height = nh
                            }
                            ResizeEdge.BR -> {
                                var nw = (iw + dx).coerceAtLeast(MIN_LAYER_SIZE)
                                var nh = (ih + dy).coerceAtLeast(MIN_LAYER_SIZE)
                                if (nw / nh > aspect) nw = nh * aspect else nh = nw / aspect
                                sel.width = nw; sel.height = nh
                            }
                            // ── Sides: directional only ──
                            ResizeEdge.T -> {
                                val nh = (ih - dy).coerceAtLeast(MIN_LAYER_SIZE)
                                sel.y = iy + ih - nh; sel.height = nh
                            }
                            ResizeEdge.B -> {
                                sel.height = (ih + dy).coerceAtLeast(MIN_LAYER_SIZE)
                            }
                            ResizeEdge.L -> {
                                val nw = (iw - dx).coerceAtLeast(MIN_LAYER_SIZE)
                                sel.x = ix + iw - nw; sel.width = nw
                            }
                            ResizeEdge.R -> {
                                sel.width = (iw + dx).coerceAtLeast(MIN_LAYER_SIZE)
                            }
                        }
                        p.consume()
                    }
                    return@awaitEachGesture
                }

                // ─── ② SELECT + LAYER DRAG / TRANSFORM ─────────────
                state.selectLayerAt(cDown.x, cDown.y)
                val layer = state.selectedLayer
                val hitLayer = layer != null && layer.containsPoint(cDown.x, cDown.y)

                if (hitLayer) {
                    val downX = down.position.x; val downY = down.position.y
                    val initX = layer.x; val initY = layer.y
                    var layerTransform = false
                    var prevAngle = 0f
                    var baseRot = layer.rotate
                    var fellThrough = false

                    down.consume()

                    gestureLoop@ while (true) {
                        val ev = awaitPointerEvent(PointerEventPass.Main)
                        if (ev.changes.all { !it.pressed }) break
                        val active = ev.changes.filter { it.pressed }

                        if (active.size >= 2) {
                            val p0c = screenToCanvas(active[0].position.x, active[0].position.y)
                            val p1c = screenToCanvas(active[1].position.x, active[1].position.y)
                            val bothOn = layer.containsPoint(p0c.x, p0c.y) &&
                                    layer.containsPoint(p1c.x, p1c.y)

                            if (bothOn) {
                                if (!layerTransform) {
                                    layerTransform = true
                                    baseRot = layer.rotate
                                    prevAngle = angleBetween(active[0].position, active[1].position)
                                }
                                val angle = angleBetween(active[0].position, active[1].position)
                                layer.rotate = baseRot +
                                        Math.toDegrees((angle - prevAngle).toDouble()).toFloat()
                                prevAngle = angle
                                active.forEach { it.consume() }
                            } else {
                                fellThrough = true
                                break@gestureLoop
                            }
                        } else if (active.size == 1 && !layerTransform) {
                            val p = active.first()
                            layer.x = initX + (p.position.x - downX) / zoom
                            layer.y = initY + (p.position.y - downY) / zoom
                            active.forEach { it.consume() }
                        }
                    }

                    if (!fellThrough) return@awaitEachGesture
                    // fellThrough → continue to canvas pan/zoom below
                }

                // ─── ③ CANVAS PAN / ZOOM ──────────────────────────
                down.consume()
                var initPan = panOffset
                var lastCount = 1
                var pz0 = zoom; var pp0 = panOffset
                var ps0 = 0f; var pm0 = Offset.Zero
                var singleRef = down.position

                while (true) {
                    val ev = awaitPointerEvent(PointerEventPass.Main)
                    if (ev.changes.all { !it.pressed }) break
                    val active = ev.changes.filter { it.pressed }

                    if (active.size >= 2 && lastCount < 2) {
                        ps0 = Offset(
                            active[0].position.x - active[1].position.x,
                            active[0].position.y - active[1].position.y
                        ).getDistance()
                        pz0 = zoom; pp0 = panOffset
                        pm0 = Offset(
                            (active[0].position.x + active[1].position.x) / 2f,
                            (active[0].position.y + active[1].position.y) / 2f
                        )
                    }

                    if (active.size >= 2 && ps0 > 0f) {
                        val span = Offset(
                            active[0].position.x - active[1].position.x,
                            active[0].position.y - active[1].position.y
                        ).getDistance()
                        val mid = Offset(
                            (active[0].position.x + active[1].position.x) / 2f,
                            (active[0].position.y + active[1].position.y) / 2f
                        )
                        val nz = (pz0 * (span / ps0)).coerceIn(MIN_ZOOM, MAX_ZOOM)
                        val r = nz / pz0
                        zoom = nz
                        panOffset = Offset(
                            pm0.x - (pm0.x - pp0.x) * r + (mid.x - pm0.x),
                            pm0.y - (pm0.y - pp0.y) * r + (mid.y - pm0.y)
                        )
                    } else if (active.size == 1) {
                        if (lastCount >= 2) {
                            initPan = panOffset
                            singleRef = active.first().position
                        }
                        val p = active.first()
                        panOffset = initPan + Offset(
                            p.position.x - singleRef.x,
                            p.position.y - singleRef.y
                        )
                    }
                    lastCount = active.size
                    active.forEach { it.consume() }
                }
            }
        }
    ) {
        drawRect(Color.Black)
        inset(
            left = state.maxUiInsets.left.floatValue + 50,
            top = state.maxUiInsets.top.floatValue + 50,
            right = state.maxUiInsets.right.floatValue + 50,
            bottom = state.maxUiInsets.bottom.floatValue + 50
        ) {
            drawContext.canvas.save()
            drawContext.canvas.translate(panOffset.x, panOffset.y)
            drawContext.canvas.scale(zoom, zoom)
            val cw = state.canvasWidth.intValue.toFloat()
            val ch = state.canvasHeight.intValue.toFloat()
            drawRect(color = Color.White, size = Size(cw, ch))
            clipRect(left = 0f, top = 0f, right = cw, bottom = ch) {
                for (l in state.layers) { if (l.visible) l.draw(this) }
            }
            val s = state.selectedLayer
            if (s != null && s.visible && s in state.layers) {
                drawMarchingAnts(s, borderPhase)
                drawResizeHandles(s)
            }
            drawContext.canvas.restore()
        }
    }
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

private fun detectResizeEdge(px: Float, py: Float, layer: Layer): ResizeEdge? {
    val l = layer.x; val t = layer.y
    val r = layer.x + layer.width; val b = layer.y + layer.height
    val h = CORNER_HIT_SIZE
    return when {
        // Corners (higher priority)
        px >= l - h && px <= l + h && py >= t - h && py <= t + h -> ResizeEdge.TL
        px >= r - h && px <= r + h && py >= t - h && py <= t + h -> ResizeEdge.TR
        px >= l - h && px <= l + h && py >= b - h && py <= b + h -> ResizeEdge.BL
        px >= r - h && px <= r + h && py >= b - h && py <= b + h -> ResizeEdge.BR
        // Sides
        px in l..r && py >= t - h && py <= t + h -> ResizeEdge.T
        px in l..r && py >= b - h && py <= b + h -> ResizeEdge.B
        px >= l - h && px <= l + h && py in t..b -> ResizeEdge.L
        px >= r - h && px <= r + h && py in t..b -> ResizeEdge.R
        else -> null
    }
}

private fun angleBetween(p0: Offset, p1: Offset): Float =
    kotlin.math.atan2((p1.y - p0.y).toDouble(), (p1.x - p0.x).toDouble()).toFloat()

private fun DrawScope.drawResizeHandles(layer: Layer) {
    val corners = listOf(
        Offset(layer.x, layer.y), Offset(layer.x + layer.width, layer.y),
        Offset(layer.x, layer.y + layer.height), Offset(layer.x + layer.width, layer.y + layer.height)
    )
    val sides = listOf(
        Offset(layer.x + layer.width / 2, layer.y),
        Offset(layer.x + layer.width, layer.y + layer.height / 2),
        Offset(layer.x + layer.width / 2, layer.y + layer.height),
        Offset(layer.x, layer.y + layer.height / 2)
    )
    for (c in corners) {
        drawCircle(color = Color.White, radius = 6f, center = c)
        drawCircle(color = Color.Black, radius = 6f, center = c, style = Stroke(width = 2f))
    }
    for (s in sides) {
        drawCircle(color = Color.White, radius = 4f, center = s)
        drawCircle(color = Color.Black, radius = 4f, center = s, style = Stroke(width = 2f))
    }
}

private fun DrawScope.drawMarchingAnts(layer: Layer, phase: Float) {
    val pad = 4f
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(layer.x - pad, layer.y - pad)
        lineTo(layer.x + layer.width + pad, layer.y - pad)
        lineTo(layer.x + layer.width + pad, layer.y + layer.height + pad)
        lineTo(layer.x - pad, layer.y + layer.height + pad)
        close()
    }
    val d = 10f; val g = 6f; val w = 4f
    drawPath(path, Color.Black, style = Stroke(width = w, pathEffect = PathEffect.dashPathEffect(floatArrayOf(d, g), phase)))
    drawPath(path, Color(0xFF808080), style = Stroke(width = w, pathEffect = PathEffect.dashPathEffect(floatArrayOf(d, g), phase + d + g)))
}
