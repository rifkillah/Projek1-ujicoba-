package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Apple Liquid Glass Aesthetics Color Palette
object GlassColors {
    // Elegant background gradient colors
    val DarkSlateBg = Color(0xFF0F172A) // Sleek slate navy
    val SoftIndigo = Color(0xFF6366F1)
    val BrightIndigo = Color(0xFF4F46E5)
    val DeepPurple = Color(0xFF4338CA)
    val GlowViolet = Color(0xFF8B5CF6)
    val GlassPink = Color(0xFFEC4899)
    val CoralPeach = Color(0xFFF43F5E)
    val EmeraldTeal = Color(0xFF10B981)
    val WaveCyan = Color(0xFF06B6D4)

    // Glass panel translucent colors
    val WhiteGlassBackground = Color(0x1BFFFFFF)  // translucent white 10%
    val WhiteGlassSecondary = Color(0x0EFFFFFF)   // translucent white 5%
    val GlassBorderLight = Color(0x4DFFFFFF)       // translucent white border 30%
    val GlassBorderDim = Color(0x13FFFFFF)         // translucent white border ~7%

    // Text & high contrast colors on glass
    val TextPrimary = Color(0xFFF1F5F9)            // slate 100
    val TextSecondary = Color(0xFF94A3B8)          // slate 400
    val TextAccent = Color(0xFF38BDF8)             // slate accent sky
    val GlowGreen = Color(0xFF34D399)

    // Liquid Gradients for vibrant screen backdrops
    val MagicAuraGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF31102F), // Dark rich plum Purple
            Color(0xFF0E1026), // Deep cosmic state
            Color(0xFF122C34)  // Mystic Teal
        )
    )

    val SunsetGlassGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF4E0E2B), // Sunset Plum
            Color(0xFF1E1B4B), // Dark Navy Indigo
            Color(0xFF064E3B)  // Forest Deep
        )
    )

    val CoolGlassGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0F172A), // Dark slate
            Color(0xFF1E293B),
            Color(0xFF0284C7)  // Ocean sky line
        )
    )

    val PremiumAuntumnGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1F1206), // burnt umber
            Color(0xFF0F172A),
            Color(0xFF831843)  // velvet rose
        )
    )
}

// Fluent Custom Modifiers for Native Liquid Glass
fun Modifier.appleLiquidGlass(
    shape: Shape,
    shadowElevation: Dp = 8.dp,
    borderWidth: Dp = 1.dp,
    glassColor: Color = GlassColors.WhiteGlassBackground
): Modifier = this
    .shadow(
        elevation = shadowElevation,
        shape = shape,
        clip = false,
        ambientColor = Color.Black.copy(alpha = 0.3f),
        spotColor = Color.Black.copy(alpha = 0.5f)
    )
    .background(
        color = glassColor,
        shape = shape
    )
    .border(
        width = borderWidth,
        brush = Brush.linearGradient(
            colors = listOf(
                GlassColors.GlassBorderLight,
                GlassColors.GlassBorderDim
            )
        ),
        shape = shape
    )
    .clip(shape)
