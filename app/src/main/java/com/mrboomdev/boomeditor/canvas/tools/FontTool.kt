package com.mrboomdev.boomeditor.canvas.tools

import com.mrboomdev.boomeditor.R
import com.mrboomdev.boomeditor.canvas.layer.Layer
import com.mrboomdev.boomeditor.canvas.layer.TextLayer
import kotlin.reflect.KClass

class FontTool: Tool.Edit {
    override val target = TextLayer::class
    override val icon = R.drawable.format_size_24px
    override val title = "Font"
}