package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.models.Story
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun StoryViewerDialog(
    story: Story,
    onDismiss: () -> Unit,
    onSendReply: (String) -> Unit = {}
) {
    var currentSegmentIndex by remember { mutableIntStateOf(0) }
    var replyText by remember { mutableStateOf("") }
    var isLikedStory by remember { mutableStateOf(false) }

    val segments = story.segments
    val currentSegment = segments.getOrNull(currentSegmentIndex) ?: return

    val progress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // Auto-advance progress timer
    LaunchedEffect(currentSegmentIndex) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = currentSegment.durationSeconds * 1000,
                easing = LinearEasing
            )
        )
        if (currentSegmentIndex < segments.size - 1) {
            currentSegmentIndex++
        } else {
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("story_viewer_dialog")
        ) {
            // Story Media (Fills screen)
            if (currentSegment.mediaResId != null) {
                Image(
                    painter = painterResource(id = currentSegment.mediaResId),
                    contentDescription = "Story Media",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else if (!currentSegment.mediaUrl.isNullOrBlank()) {
                AsyncImage(
                    model = currentSegment.mediaUrl,
                    contentDescription = "Story Media",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Dark vignette gradients on top and bottom for readability
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                        )
                    )
            )

            // Touch gesture layers: Tap left for previous, tap right for next
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    if (currentSegmentIndex > 0) {
                                        currentSegmentIndex--
                                    }
                                }
                            )
                        }
                )
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    if (currentSegmentIndex < segments.size - 1) {
                                        currentSegmentIndex++
                                    } else {
                                        onDismiss()
                                    }
                                }
                            )
                        }
                )
            }

            // Content Overlay (Progress bars + Header + Bottom Bar)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top section: Progress Bars & Author Header
                Column {
                    // Segmented Progress Bars
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        segments.forEachIndexed { index, _ ->
                            val segmentProgress = when {
                                index < currentSegmentIndex -> 1f
                                index == currentSegmentIndex -> progress.value
                                else -> 0f
                            }
                            LinearProgressIndicator(
                                progress = { segmentProgress },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = GoldGlow,
                                trackColor = Color.White.copy(alpha = 0.3f),
                            )
                        }
                    }

                    // Author Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, GoldPrimary, CircleShape)
                                    .padding(2.dp)
                            ) {
                                if (story.author.avatarResId != null) {
                                    Image(
                                        painter = painterResource(id = story.author.avatarResId),
                                        contentDescription = story.author.username,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = story.author.username,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    if (story.author.isVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.WorkspacePremium,
                                            contentDescription = "VIP",
                                            tint = GoldGlow,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = story.timestamp,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_story_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Story",
                                tint = TextPrimary
                            )
                        }
                    }
                }

                // Bottom Section: Caption & Quick Reply Input
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    if (currentSegment.caption.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.65f),
                                    RoundedCornerShape(12.dp)
                                )
                                .border(1.dp, DarkBorderHighlight, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = currentSegment.caption,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Reply Input + Like Reaction
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = replyText,
                            onValueChange = { replyText = it },
                            placeholder = {
                                Text(
                                    "Send message to ${story.author.username}...",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("story_reply_input"),
                            shape = RoundedCornerShape(25.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSecondary.copy(alpha = 0.8f),
                                unfocusedContainerColor = DarkSecondary.copy(alpha = 0.6f),
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            trailingIcon = {
                                if (replyText.isNotBlank()) {
                                    IconButton(
                                        onClick = {
                                            onSendReply(replyText)
                                            replyText = ""
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = "Send Reply",
                                            tint = GoldGlow
                                        )
                                    }
                                }
                            },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Heart reaction button
                        IconButton(
                            onClick = { isLikedStory = !isLikedStory },
                            modifier = Modifier
                                .size(48.dp)
                                .background(DarkSecondary.copy(alpha = 0.8f), CircleShape)
                                .border(1.dp, if (isLikedStory) GoldGlow else DarkBorder, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "React Heart",
                                tint = if (isLikedStory) LikeRed else TextPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
