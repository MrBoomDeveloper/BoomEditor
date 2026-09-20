package com.mrboomdev.boomeditor.ui

import androidx.annotation.DrawableRes

data class MainAction(
    val text: String,
    @DrawableRes val icon: Int,
    val action: (() -> Unit)? = null,
    val dropdown: List<MainAction>? = null
)