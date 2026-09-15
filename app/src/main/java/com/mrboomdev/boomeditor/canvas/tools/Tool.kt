package com.mrboomdev.boomeditor.canvas.tools

import androidx.annotation.DrawableRes
import com.mrboomdev.boomeditor.canvas.layer.Layer
import kotlin.reflect.KClass

sealed interface Tool {
    @get:DrawableRes
    val icon: Int
    
    val title: String
    
    interface Add: Tool
    interface Transform: Tool
    interface Filter: Tool

    interface Edit: Tool {
        val target: KClass<out Layer>
    }
}