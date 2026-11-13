package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.models.Story
import com.example.data.models.User
import com.example.ui.theme.*

@Composable
fun StoryTray(
    stories: List<Story>,
    currentUser: User,
    onStoryClick: (Story) -> Unit,
    onAddStoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkBackground)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        // "Your Story" item with plus badge
        item {
            YourStoryItem(
                currentUser = currentUser,
                onClick = onAddStoryClick
            )
        }

        // Other Creators Stories
        items(stories, key = { it.id }) { story ->
            StoryCircleItem(
                story = story,
                onClick = { onStoryClick(story) }
            )
        }
    }
}

@Composable
private fun YourStoryItem(
    currentUser: User,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick)
            .testTag("story_my_story")
    ) {
        Box(
            modifier = Modifier.size(68.dp),
            contentAlignment = Alignment.Center
        ) {
            // Subtle ring
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, GoldPrimary.copy(alpha = 0.5f), CircleShape)
                    .padding(3.dp)
            ) {
                if (currentUser.avatarResId != null) {
                    Image(
                        painter = painterResource(id = currentUser.avatarResId),
                        contentDescription = "My Avatar",
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

            // Glowing Golden Plus Badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(22.dp)
                    .background(GoldenGradient, CircleShape)
                    .border(2.dp, DarkBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Story",
                    tint = DarkBackground,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Your Story",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun StoryCircleItem(
    story: Story,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick)
            .testTag("story_item_${story.id}")
    ) {
        Box(
            modifier = Modifier.size(68.dp),
            contentAlignment = Alignment.Center
        ) {
            // Story Ring: Glowing Gold Gradient if Unviewed, Muted Dark if Viewed
            val ringModifier = if (!story.isViewed) {
                Modifier
                    .size(66.dp)
                    .clip(CircleShape)
                    .background(StoryRingGradient)
                    .padding(2.5.dp)
            } else {
                Modifier
                    .size(66.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, DarkBorder, CircleShape)
                    .padding(3.dp)
            }

            Box(
                modifier = ringModifier
                    .background(DarkBackground, CircleShape)
                    .padding(2.dp)
            ) {
                val avatarRes = story.author.avatarResId
                if (avatarRes != null) {
                    Image(
                        painter = painterResource(id = avatarRes),
                        contentDescription = story.author.username,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else if (!story.author.avatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = story.author.avatarUrl,
                        contentDescription = story.author.username,
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

            // VIP Gold Crown on top right
            if (story.author.isVip) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .background(DarkSecondary, CircleShape)
                        .border(1.dp, GoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "VIP",
                        tint = GoldGlow,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = story.author.username,
            color = if (!story.isViewed) TextPrimary else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (!story.isViewed) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
