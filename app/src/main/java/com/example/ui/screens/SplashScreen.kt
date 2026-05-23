package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassColors
import com.example.ui.theme.appleLiquidGlass
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Beautiful shifting aesthetic circle animations
    val offset1X = remember { Animatable(0f) }
    val offset1Y = remember { Animatable(0f) }
    val offset2X = remember { Animatable(0f) }
    val offset2Y = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Run floating glow animation
        launch {
            offset1X.animateTo(
                targetValue = 180f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
        launch {
            offset1Y.animateTo(
                targetValue = 120f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
        launch {
            offset2X.animateTo(
                targetValue = -150f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
        launch {
            offset2Y.animateTo(
                targetValue = -200f,
                animationSpec = infiniteRepeatable(
                    animation = tween(5000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }

        // Automatic redirection after delay
        delay(2500)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassColors.MagicAuraGradient)
    ) {
        // Glowing background blobs to make the glass effect visible
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = GlassColors.GlowViolet.copy(alpha = 0.5f),
                radius = 280.dp.toPx(),
                center = Offset(
                    x = size.width / 2 + offset1X.value.dp.toPx(),
                    y = size.height / 3 + offset1Y.value.dp.toPx()
                )
            )
            drawCircle(
                color = GlassColors.CoralPeach.copy(alpha = 0.45f),
                radius = 250.dp.toPx(),
                center = Offset(
                    x = size.width / 3 + offset2X.value.dp.toPx(),
                    y = size.height * 2 / 3 + offset2Y.value.dp.toPx()
                )
            )
        }

        // Concentrated glass card centering the branding
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .size(width = 300.dp, height = 300.dp)
                .appleLiquidGlass(
                    shape = RoundedCornerShape(32.dp),
                    shadowElevation = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // Beautiful glowing app icon inside extra glossy layer
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .appleLiquidGlass(
                        shape = RoundedCornerShape(20.dp),
                        glassColor = Color(0x33FFFFFF)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = "PocketLib logo",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Name with pristine Apple display typography
            Text(
                text = "PocketLib",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = GlassColors.TextPrimary,
                letterSpacing = 1.5.sp,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Katalog & Catatan Membaca",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = GlassColors.TextSecondary.copy(alpha = 0.85f),
                fontFamily = FontFamily.SansSerif
            )
        }

        // Version caption at bottom
        Text(
            text = "v1.0 • Apple Glass Design",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .graphicsLayer(alpha = 0.5f),
            color = GlassColors.TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = 1.sp
        )
    }
}
