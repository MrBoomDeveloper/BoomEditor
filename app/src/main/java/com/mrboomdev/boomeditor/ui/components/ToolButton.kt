package com.mrboomdev.boomeditor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrboomdev.boomeditor.R
import com.mrboomdev.boomeditor.ui.BoomEditorTheme

@Composable
fun ToolButton(
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.defaultMinSize(
            minWidth = 72.dp, 
            minHeight = 64.dp
        ),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
    ) { 
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = icon,
                contentDescription = null
            )

            Text(
                style = MaterialTheme.typography.labelSmall,
                text = title,
            )
        }
    }
}

@Preview
@Composable
private fun ToolButtonPreview() {
    BoomEditorTheme { 
        ToolButton(
            icon = painterResource(R.drawable.crop_24px),
            title = "Crop",
            onClick = {}
        )
    }
}