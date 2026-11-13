package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.Reel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ReelsScreen(
    reels: List<Reel>,
    onLikeReel: (String) -> Unit,
    onBookmarkReel: (String) -> Unit,
    onCommentReel: (Reel) -> Unit,
    onShareReel: (Reel) -> Unit,
    onFollowClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { reels.size })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("reels_screen")
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val reel = reels[page]
            ReelItemView(
                reel = reel,
                onLike = { onLikeReel(reel.id) },
                onBookmark = { onBookmarkReel(reel.id) },
                onComment = { onCommentReel(reel) },
                onShare = { onShareReel(reel) },
                onFollow = { onFollowClick(reel.author.id) }
            )
        }

        // Top Header: "Reels" in Gold
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Reels",
                color = GoldGlow,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Create Reel",
                tint = TextPrimary,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun ReelItemView(
    reel: Reel,
    onLike: () -> Unit,
    onBookmark: () -> Unit,
    onComment: () -> Unit,
    onShare: () -> Unit,
    onFollow: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var isCaptionExpanded by remember { mutableStateOf(false) }
    var showHeartBurst by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val heartScale by animateFloatAsState(
        targetValue = if (showHeartBurst) 1.3f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "reelHeartScale"
    )

    // Rotating Audio Disc Animation
    val infiniteTransition = rememberInfiniteTransition(label = "audioDisc")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "audioRotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (!reel.isLiked) {
                            onLike()
                        }
                        showHeartBurst = true
                        scope.launch {
                            delay(900)
                            showHeartBurst = false
                        }
                    },
                    onTap = {
                        isPlaying = !isPlaying
                    }
                )
            }
            .testTag("reel_item_${reel.id}")
    ) {
        // Media (Full screen vertical)
        if (reel.mediaResId != null) {
            Image(
                painter = painterResource(id = reel.mediaResId),
                contentDescription = reel.caption,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else if (!reel.mediaUrl.isNullOrBlank()) {
            AsyncImage(
                model = reel.mediaUrl,
                contentDescription = reel.caption,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Gradient Shadows for Top and Bottom readability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
        )

        // Heart Burst on Double Tap
        if (showHeartBurst) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = GoldGlow,
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.Center)
                    .scale(heartScale)
            )
        }

        // Play / Pause Indicator
        if (!isPlaying) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Center)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Paused",
                    tint = TextPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // Right Action Column (Like, Comment, Share, Save, Rotating Disc)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Like
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onLike,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = if (reel.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like Reel",
                        tint = if (reel.isLiked) LikeRed else TextPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = "%,d".format(reel.likesCount),
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Comment
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onComment,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comment Reel",
                        tint = TextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = reel.commentsCount.toString(),
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Share
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Share Reel",
                        tint = TextPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Text(
                    text = reel.sharesCount.toString(),
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Save / Bookmark
            IconButton(
                onClick = onBookmark,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = if (reel.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Save Reel",
                    tint = if (reel.isBookmarked) GoldGlow else TextPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Rotating Luxury Vinyl Disc
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(DarkSecondary)
                    .border(2.dp, GoldPrimary, CircleShape)
                    .rotate(rotation),
                contentAlignment = Alignment.Center
            ) {
                if (reel.author.avatarResId != null) {
                    Image(
                        painter = painterResource(id = reel.author.avatarResId),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(0.7f)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // Bottom Left Info: Author, Caption, Audio Ticker
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.82f)
                .padding(start = 16.dp, bottom = 24.dp)
        ) {
            // Author row with Follow button
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, GoldPrimary, CircleShape)
                        .padding(2.dp)
                ) {
                    if (reel.author.avatarResId != null) {
                        Image(
                            painter = painterResource(id = reel.author.avatarResId),
                            contentDescription = reel.author.username,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = reel.author.username,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                if (reel.author.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "VIP",
                        tint = GoldGlow,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                if (!reel.author.isCurrentUser) {
                    OutlinedButton(
                        onClick = onFollow,
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, GoldPrimary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (reel.author.isFollowing) DarkSurfaceVariant else Color.Transparent,
                            contentColor = if (reel.author.isFollowing) TextSecondary else GoldGlow
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = if (reel.author.isFollowing) "Following" else "Follow",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Caption
            Text(
                text = reel.caption,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = if (isCaptionExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { isCaptionExpanded = !isCaptionExpanded }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Audio Music Track Information
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Audio",
                    tint = GoldGlow,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = reel.audioTitle,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
