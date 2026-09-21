package com.mrboomdev.boomeditor.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun InfiniteRulerSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Float = 0f,
    maxValue: Float = Float.MAX_VALUE,
    stepValue: Float = 1f, // How much value changes per tick
    tickSpacing: Dp = 10.dp, // Pixel distance between tick marks
    lineColor: Color = MaterialTheme.colorScheme.secondary,
    indicatorColor: Color = MaterialTheme.colorScheme.primary,
    height: Dp = 48.dp
) {
    val density = LocalDensity.current
    val tickSpacingPx = remember(tickSpacing, density) {
        with(density) { tickSpacing.toPx() }
    }

    // Accumulates scroll deltas until they cross a full tick spacing threshold
    var accumulatedPx by remember { mutableFloatStateOf(0f) }

    val scrollableState = rememberScrollableState { deltaPx ->
        // Dragging left (negative delta) increases the value; dragging right decreases it.
        accumulatedPx -= deltaPx

        val stepsMoved = (accumulatedPx / tickSpacingPx).toInt()
        if (stepsMoved != 0) {
            val newValue = (value + stepsMoved * stepValue)
                .coerceIn(minValue, maxValue)

            onValueChange(newValue)
            // Keep fractional remainder to prevent losing sub-tick precision
            accumulatedPx -= stepsMoved * tickSpacingPx
        }
        deltaPx // Consume all gesture delta
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .scrollable(
                state = scrollableState,
                orientation = Orientation.Horizontal
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            val width = size.width
            val centerY = size.height / 2
            val centerX = width / 2

            // Offset alignment so current value matches center indicator line
            val offset = (value % stepValue) / stepValue * tickSpacingPx + accumulatedPx
            val visibleTicksOnEachSide = (centerX / tickSpacingPx).roundToInt() + 1

            val currentStepIndex = (value / stepValue).toInt()

            for (i in -visibleTicksOnEachSide..visibleTicksOnEachSide) {
                val tickIndex = currentStepIndex + i
                val tickValue = tickIndex * stepValue

                // Skip rendering out-of-bounds ticks
                if (tickValue !in minValue..maxValue) continue

                val x = centerX + (i * tickSpacingPx) - offset

                if (x in 0f..width) {
                    val isMajor = tickIndex % 5 == 0
                    val lineHeight = if (isMajor) centerY * 0.8f else centerY * 0.4f
                    val strokeWidth = if (isMajor) 5f else 2.5f

                    drawLine(
                        color = lineColor,
                        start = Offset(x, centerY - lineHeight / 2),
                        end = Offset(x, centerY + lineHeight / 2),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Draw Center Indicator Pin
            drawLine(
                color = indicatorColor,
                start = Offset(centerX, centerY - (height.toPx() * 0.4f)),
                end = Offset(centerX, centerY + (height.toPx() * 0.4f)),
                strokeWidth = 24f,
                cap = StrokeCap.Round
            )
        }
    }
}