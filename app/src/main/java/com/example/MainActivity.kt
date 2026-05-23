package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.PocketLibMainContainer
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.GlassColors
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Main visual context container with beautiful dynamic cosmic gradient back drop
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(GlassColors.SunsetGlassGradient)
                ) {
                    PocketLibNavigationApp()
                }
            }
        }
    }
}

@Composable
fun PocketLibNavigationApp() {
    val viewModel: com.example.ui.viewmodel.PocketLibViewModel = viewModel()
    val currentUser by viewModel.currentUser.collectAsState()

    // Splash navigation state manager
    var currentScreen by remember { mutableStateOf("splash") }

    when (currentScreen) {
        "splash" -> {
            SplashScreen(
                onSplashFinished = {
                    currentScreen = if (currentUser != null) "home" else "auth"
                }
            )
        }
        "auth" -> {
            AuthScreen(
                viewModel = viewModel,
                onAuthSuccess = {
                    currentScreen = "home"
                }
            )
        }
        "home" -> {
            // Guard state if currentUser is logged out, safely route back to Auth screen
            if (currentUser == null) {
                currentScreen = "auth"
            } else {
                PocketLibMainContainer(viewModel = viewModel)
            }
        }
    }
}
