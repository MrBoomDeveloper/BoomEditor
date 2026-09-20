package com.mrboomdev.boomeditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mrboomdev.boomeditor.ui.AppScreen
import com.mrboomdev.boomeditor.ui.BoomEditorTheme
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FileKit.init(this)
        
        val editorState = EditorState()
        
        setContent {
            BoomEditorTheme {
                AppScreen(editorState)
            }
        }
    }
}