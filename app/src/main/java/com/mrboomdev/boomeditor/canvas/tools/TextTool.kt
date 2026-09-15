package com.mrboomdev.boomeditor.canvas.tools

import com.mrboomdev.boomeditor.R
import com.mrboomdev.boomeditor.canvas.layer.TextLayer

class TextTool: Tool.Edit {
    override val target = TextLayer::class
    override val title = "Text"
    override val icon = R.drawable.text_select_end_24px
}