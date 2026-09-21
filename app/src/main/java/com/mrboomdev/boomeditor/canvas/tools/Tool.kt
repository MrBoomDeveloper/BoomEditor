package com.mrboomdev.boomeditor.canvas.tools

import androidx.annotation.DrawableRes
import com.mrboomdev.boomeditor.canvas.layer.Layer
import kotlin.reflect.KClass

abstract class Tool(
    @DrawableRes
    val icon: Int,
    val title: String
) {
    
}