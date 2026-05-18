package com.roomie.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.roomie.app.core.navigation.NavGraph
import com.roomie.app.core.ui.theme.RoomieTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoomieTheme {
                NavGraph()
            }
        }
    }
}
