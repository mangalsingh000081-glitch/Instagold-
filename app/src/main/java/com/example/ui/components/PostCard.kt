package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.data.models.Comment
import com.example.data.models.Post
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PostCard(
    post: Post,
    onLikeClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onTranslateComment: (commentId: String) -> Unit,
    onFollowClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isCaptionExpanded by remember { mutableStateOf(false) }
    var showHeartBurst by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val heartScale by animateFloatAsState(
        targetValue = if (showHeartBurst) 1.25f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "heartScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("post_card_${post.id}"),
        colors = CardDefaults.cardColors(
            containerColor = DarkSecondary
        ),
        shape = RoundedCornerShape(0.dp) // Edge-to-edge sleek Instagram feed style
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // 1. Post Header (Author info + VIP Badge + Location)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Author Avatar with thin gold ring
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, GoldPrimary, CircleShape)
                            .padding(2.dp)
                    ) {
                        if (post.author.avatarResId != null) {
                            Image(
                                painter = painterResource(id = post.author.avatarResId),
                                contentDescription = post.author.displayName,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else if (!post.author.avatarUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = post.author.avatarUrl,
                                contentDescription = post.author.displayName,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(DarkSurfaceVariant, CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.author.username,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (post.author.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = "Verified Gold",
                                    tint = GoldGlow,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        if (!post.location.isNullOrBlank()) {
                            Text(
                                text = post.location,
                                color = GoldPrimary.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Follow toggle button if not current user
                    if (!post.author.isCurrentUser) {
                        OutlinedButton(
                            onClick = onFollowClick,
                            modifier = Modifier
                                .height(30.dp)
                                .testTag("follow_button_${post.author.id}"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (post.author.isFollowing) DarkSurfaceVariant else Color.Transparent,
                                contentColor = if (post.author.isFollowing) TextSecondary else GoldGlow
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (post.author.isFollowing) DarkBorder else GoldPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = if (post.author.isFollowing) "Following" else "Follow",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 2. Media Image with Double-Tap to Like & Heart Animation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(DarkBackground)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (!post.isLiked) {
                                    onLikeClick()
                                }
                                showHeartBurst = true
                                scope.launch {
                                    delay(900)
                                    showHeartBurst = false
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (post.mediaResId != null) {
                    Image(
                        painter = painterResource(id = post.mediaResId),
                        contentDescription = "Post Media",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (!post.mediaUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = post.mediaUrl,
                        contentDescription = "Post Media",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Heart Burst Animation on Double Tap
                if (showHeartBurst) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Liked",
                        tint = GoldGlow,
                        modifier = Modifier
                            .size(110.dp)
                            .scale(heartScale)
                    )
                }

                // Subtle Filter Tag if active
                if (post.filterName != "Normal") {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
                            .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = post.filterName,
                            color = GoldLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 3. Actions Row (Like, Comment, Share, Bookmark)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Like Button
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.testTag("like_post_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLiked) LikeRed else TextPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Comment Button
                    IconButton(
                        onClick = onCommentClick,
                        modifier = Modifier.testTag("comment_post_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comment",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Share Button
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.testTag("share_post_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Send,
                            contentDescription = "Share",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Bookmark Button
                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier.testTag("bookmark_post_${post.id}")
                ) {
                    Icon(
                        imageVector = if (post.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (post.isBookmarked) GoldGlow else TextPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // 4. Likes Count with Golden Tone
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${"%,d".format(post.likesCount)} likes",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• VIP Verified",
                    color = GoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // 5. Caption with Author and Hashtags
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = buildString {
                        append(post.author.username)
                        append(" ")
                        append(post.caption)
                    },
                    color = TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = if (isCaptionExpanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (post.caption.length > 80 && !isCaptionExpanded) {
                    Text(
                        text = "more",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { isCaptionExpanded = true }
                            .padding(top = 2.dp)
                    )
                }

                // Gold Hashtags
                if (post.hashtags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = post.hashtags.joinToString(" "),
                        color = GoldGlow.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 6. Comments Preview & Inline Translation
            if (post.comments.isNotEmpty()) {
                val topComment = post.comments.first()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = buildString {
                                append(topComment.author.username)
                                append(" ")
                                val displayedText = when (topComment.currentLanguage) {
                                    "hi" -> topComment.hindiTranslation.ifBlank { topComment.text }
                                    "en" -> topComment.englishTranslation.ifBlank { topComment.text }
                                    else -> topComment.text
                                }
                                append(displayedText)
                            },
                            color = TextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        // Translate comment button (English <-> Hindi toggle)
                        Text(
                            text = when (topComment.currentLanguage) {
                                "hi" -> "See Original"
                                else -> "Translate (हिन्दी)"
                            },
                            color = GoldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { onTranslateComment(topComment.id) }
                                .padding(start = 8.dp)
                                .testTag("translate_comment_${topComment.id}")
                        )
                    }

                    if (post.commentsCount > 1) {
                        Text(
                            text = "View all ${post.commentsCount} comments",
                            color = TextTertiary,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .clickable(onClick = onCommentClick)
                                .padding(vertical = 4.dp)
                        )
                    }

                    // Quick comment row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable(onClick = onCommentClick),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Add a comment...",
                            color = TextTertiary,
                            fontSize = 12.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("❤️", fontSize = 12.sp)
                            Text("🔥", fontSize = 12.sp)
                            Text("⚜️", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Timestamp
            Text(
                text = post.timestamp.uppercase(),
                color = TextTertiary,
                fontSize = 10.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
            )

            // Divider between posts
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DarkBorder.copy(alpha = 0.6f))
            )
        }
    }
}
