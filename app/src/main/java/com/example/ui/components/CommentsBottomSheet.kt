package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.Comment
import com.example.data.models.Post
import com.example.data.models.User
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    post: Post,
    currentUser: User,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit,
    onTranslateComment: (commentId: String) -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }
    val luxuryEmojis = listOf("⚜️", "👑", "✨", "🥂", "🏆", "🔥", "💎")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSecondary,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = GoldPrimary.copy(alpha = 0.5f)
            )
        },
        modifier = Modifier.testTag("comments_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Comments (${post.commentsCount})",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DarkBorder)
            )

            // Comments List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (post.comments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No comments yet. Be the first to compliment this masterpiece. ⚜️",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(post.comments, key = { it.id }) { comment ->
                        CommentItem(
                            comment = comment,
                            onTranslate = { onTranslateComment(comment.id) }
                        )
                    }
                }
            }

            // Quick Luxury Emojis Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(luxuryEmojis) { emoji ->
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(DarkSecondary)
                            .clickable { newCommentText += emoji }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 18.sp)
                    }
                }
            }

            // Input Bar
            Surface(
                color = DarkSecondary,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, GoldPrimary, CircleShape)
                    ) {
                        if (currentUser.avatarResId != null) {
                            Image(
                                painter = painterResource(id = currentUser.avatarResId),
                                contentDescription = "Me",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = {
                            Text(
                                "Add a comment as ${currentUser.username}...",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("comment_input_field"),
                        shape = RoundedCornerShape(25.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant,
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                onAddComment(newCommentText)
                                newCommentText = ""
                            }
                        },
                        enabled = newCommentText.isNotBlank(),
                        modifier = Modifier
                            .size(44.dp)
                            .then(
                                if (newCommentText.isNotBlank()) Modifier.background(GoldenGradient, CircleShape)
                                else Modifier.background(DarkSurfaceVariant, CircleShape)
                            )
                            .testTag("submit_comment_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Post Comment",
                            tint = if (newCommentText.isNotBlank()) DarkBackground else TextTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentItem(
    comment: Comment,
    onTranslate: () -> Unit
) {
    var isLiked by remember { mutableStateOf(comment.isLiked) }
    var likesCount by remember { mutableIntStateOf(comment.likesCount) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("comment_item_${comment.id}"),
        verticalAlignment = Alignment.Top
    ) {
        // Commenter Avatar
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .border(1.dp, GoldPrimary.copy(alpha = 0.6f), CircleShape)
        ) {
            if (comment.author.avatarResId != null) {
                Image(
                    painter = painterResource(id = comment.author.avatarResId),
                    contentDescription = comment.author.username,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else if (!comment.author.avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = comment.author.avatarUrl,
                    contentDescription = comment.author.username,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Comment Body
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.author.username,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                if (comment.author.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "VIP",
                        tint = GoldGlow,
                        modifier = Modifier.size(13.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = comment.timestamp,
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Display text based on translation state
            val displayedText = when (comment.currentLanguage) {
                "hi" -> comment.hindiTranslation.ifBlank { comment.text }
                "en" -> comment.englishTranslation.ifBlank { comment.text }
                else -> comment.text
            }

            Text(
                text = displayedText,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            // Translation Toggle Action
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = onTranslate)
                    .padding(vertical = 2.dp)
                    .testTag("translate_btn_${comment.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = "Translate",
                    tint = GoldPrimary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (comment.currentLanguage) {
                        "hi" -> "See Original (English)"
                        else -> "Translate to हिन्दी"
                    },
                    color = GoldGlow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Like Comment Heart
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            IconButton(
                onClick = {
                    isLiked = !isLiked
                    likesCount = if (isLiked) likesCount + 1 else likesCount - 1
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like Comment",
                    tint = if (isLiked) LikeRed else TextTertiary,
                    modifier = Modifier.size(16.dp)
                )
            }
            if (likesCount > 0) {
                Text(
                    text = likesCount.toString(),
                    color = TextTertiary,
                    fontSize = 10.sp
                )
            }
        }
    }
}
