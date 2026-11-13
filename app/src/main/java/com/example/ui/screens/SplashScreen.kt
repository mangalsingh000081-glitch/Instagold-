package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onDismiss: () -> Unit,
    autoDismissDelayMs: Long = 2800L,
    modifier: Modifier = Modifier
) {
    // Auto-advance after delay
    LaunchedEffect(Unit) {
        delay(autoDismissDelayMs)
        onDismiss()
    }

    // Infinite breathing and glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "splashTransition")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val contentAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        contentAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(700, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Ambient radial golden glow behind emblem
        Box(
            modifier = Modifier
                .size(340.dp)
                .alpha(glowAlpha)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            GoldGlow.copy(alpha = 0.25f),
                            GoldPrimary.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Main Splash Image (Emblem + "Instagold" Typography)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .scale(pulseScale)
                .alpha(contentAlpha.value)
        ) {
            Image(
                painter = painterResource(id = R.drawable.instgold_wordmark),
                contentDescription = "Instagold Splash Logo",
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(800f / 486f),
                contentScale = ContentScale.Fit
            )
        }

        // Bottom Footer: Subtle luxury tagline & prompt to tap
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp)
                .alpha(contentAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(GoldGlow, RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "THE GOLD STANDARD OF SOCIAL",
                    color = GoldLight.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(GoldGlow, RoundedCornerShape(3.dp))
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Tap anywhere to enter",
                color = TextTertiary,
                fontSize = 11.sp
            )
        }
    }
}
