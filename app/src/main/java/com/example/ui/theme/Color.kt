package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Instgold Redesigned Brand Palette
val GoldPrimary = Color(0xFFD4AF37)         // Metallic Gold
val GoldGlow = Color(0xFFFFD700)            // Golden Glow
val GoldLight = Color(0xFFFFF2A1)           // Champagne Highlight
val GoldDark = Color(0xFF997A15)            // Deep Bronze Gold
val GoldAccent = Color(0xFFE5B842)          // Vibrant Rich Gold

val DarkBackground = Color(0xFF080808)      // Deep Black (#080808)
val DarkSecondary = Color(0xFF151515)       // Secondary Background (#151515)
val DarkSurface = Color(0xFF1A1A1A)         // Surface Card
val DarkSurfaceVariant = Color(0xFF222222)  // Input / Elevated Chip
val DarkBorder = Color(0xFF262626)          // Subtle Separator
val DarkBorderHighlight = Color(0xFF42381C) // Subtle Gold-Tinged Border

val TextPrimary = Color(0xFFFFFFFF)         // Pure White
val TextSecondary = Color(0xFFA8A8A8)       // Light Grey
val TextTertiary = Color(0xFF6E6E6E)        // Muted Label
val VerifiedGold = Color(0xFFFFD700)        // Gold VIP verification badge
val ErrorRed = Color(0xFFFF4D4F)            // Error / Alert
val LikeRed = Color(0xFFFF3040)             // Instagram vibrant like red

// Golden Gradients
val GoldenGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFFDF73),
        Color(0xFFD4AF37),
        Color(0xFFAA8012)
    )
)

val GoldenGlowGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFFFE07A),
        Color(0xFFFFD700),
        Color(0xFFD4AF37)
    )
)

val StoryRingGradient = Brush.sweepGradient(
    colors = listOf(
        Color(0xFFFFD700),
        Color(0xFFD4AF37),
        Color(0xFFFFF2A1),
        Color(0xFFB8860B),
        Color(0xFFFFD700)
    )
)

val DarkGlassGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0x33D4AF37),
        Color(0x10151515)
    )
)
