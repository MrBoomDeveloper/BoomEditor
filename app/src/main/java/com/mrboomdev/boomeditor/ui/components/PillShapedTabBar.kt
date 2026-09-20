package com.mrboomdev.boomeditor.ui.components

import android.R.attr.maxWidth
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrboomdev.boomeditor.ui.BoomEditorTheme
import kotlin.collections.forEachIndexed

@Composable
fun PillShapedTabBar(
    items: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    selectedTabColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    selectedContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium
) {
    BoxWithConstraints(
        modifier = modifier
            .clip(CircleShape)
            .background(containerColor)
    ) {
        val tabWidth = maxWidth / items.size

        // Horizontal offset animation for the active pill background
        val indicatorOffset by animateDpAsState(
            targetValue = tabWidth * selectedIndex,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            label = "pill_indicator_offset"
        )

        // Sliding pill indicator
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(tabWidth)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(selectedTabColor)
        )

        // Interactive tab items overlay
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, title ->
                val isSelected = index == selectedIndex

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) selectedContentColor else contentColor,
                    label = "tab_text_color"
                )

                Box(
                    modifier = Modifier
                        .width(tabWidth)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = textColor,
                        style = textStyle,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PillShapedTabBarPreview() {
    BoomEditorTheme {
        var selectedTab by remember { mutableIntStateOf(0) }
        val tabs = remember { listOf("Day", "Week", "Month", "Year") }

        PillShapedTabBar(
            items = tabs,
            selectedIndex = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(48.dp)
        )   
    }
}