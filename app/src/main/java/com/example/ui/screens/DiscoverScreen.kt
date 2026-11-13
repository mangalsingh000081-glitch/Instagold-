package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.Post
import com.example.data.models.User
import com.example.ui.theme.*

@Composable
fun DiscoverScreen(
    posts: List<Post>,
    creators: List<User>,
    searchQuery: String,
    selectedTag: String,
    onSearchChange: (String) -> Unit,
    onTagSelect: (String) -> Unit,
    onFollowToggle: (String) -> Unit,
    onPostClick: (Post) -> Unit,
    modifier: Modifier = Modifier
) {
    val trendingTags = listOf(
        "All", "#GoldStandard", "#LuxuryLife", "#HauteHorlogerie", "#Supercars", "#YachtLife", "#Monaco"
    )

    // Filter posts based on search query or selected tag
    val filteredPosts = remember(posts, searchQuery, selectedTag) {
        posts.filter { post ->
            val matchesQuery = searchQuery.isBlank() ||
                    post.caption.contains(searchQuery, ignoreCase = true) ||
                    post.author.username.contains(searchQuery, ignoreCase = true) ||
                    post.hashtags.any { it.contains(searchQuery, ignoreCase = true) }

            val matchesTag = selectedTag == "All" ||
                    post.hashtags.any { it.equals(selectedTag, ignoreCase = true) }

            matchesQuery && matchesTag
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("discover_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Search Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            "Search luxury creators, hashtags, timepieces...",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = GoldPrimary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Search",
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("discover_search_input"),
                    shape = RoundedCornerShape(26.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSecondary,
                        unfocusedContainerColor = DarkSecondary,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )
            }
        }

        // 2. Trending Tags Horizontal Bar
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(trendingTags) { tag ->
                    val isSelected = tag == selectedTag
                    val bgMod = if (isSelected) Modifier.background(GoldenGradient) else Modifier.background(DarkSecondary)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .then(bgMod)
                            .border(
                                1.dp,
                                if (isSelected) GoldLight else DarkBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { onTagSelect(tag) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("discover_tag_$tag"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tag,
                            color = if (isSelected) DarkBackground else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 3. Featured VIP Creators Carousel
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "VIP CREATORS",
                        color = GoldGlow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Curated",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(creators) { creator ->
                        CreatorMiniCard(
                            creator = creator,
                            onFollowToggle = { onFollowToggle(creator.id) }
                        )
                    }
                }
            }
        }

        // 4. Masonry / Staggered Grid Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EXPLORE VAULT",
                    color = GoldGlow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        // 5. 2-Column Staggered Grid of Posts
        item {
            val chunked = filteredPosts.chunked(2)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                chunked.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEachIndexed { colIndex, post ->
                            // Alternate heights for masonry feel (e.g. 210dp and 170dp)
                            val cardHeight = if (colIndex % 2 == 0) 220.dp else 180.dp
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(cardHeight)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(DarkSecondary)
                                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                                    .clickable { onPostClick(post) }
                                    .testTag("discover_post_${post.id}")
                            ) {
                                if (post.mediaResId != null) {
                                    Image(
                                        painter = painterResource(id = post.mediaResId),
                                        contentDescription = post.caption,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else if (!post.mediaUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = post.mediaUrl,
                                        contentDescription = post.caption,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                // Top Right Video / Reel indicator badge on alternating tiles
                                if (colIndex % 2 == 1) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Reel",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .size(16.dp)
                                    )
                                }

                                // Bottom dark gradient overlay for info readability
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(70.dp)
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                            )
                                        )
                                )

                                // Overlay info: Creator username & like count
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "@${post.author.username}",
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = null,
                                            tint = LikeRed,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = post.likesCount.toString(),
                                            color = TextPrimary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Pad out single trailing item in row
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatorMiniCard(
    creator: User,
    onFollowToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(135.dp)
            .testTag("creator_card_${creator.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSecondary),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, GoldPrimary, CircleShape)
                    .padding(2.dp)
            ) {
                if (creator.avatarResId != null) {
                    Image(
                        painter = painterResource(id = creator.avatarResId),
                        contentDescription = creator.username,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = creator.username,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (creator.isVerified) {
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "VIP",
                        tint = GoldGlow,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Text(
                text = "${creator.followersCount} fans",
                color = TextSecondary,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onFollowToggle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (creator.isFollowing) DarkSurfaceVariant else GoldPrimary,
                    contentColor = if (creator.isFollowing) TextSecondary else DarkBackground
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (creator.isFollowing) "Following" else "Follow",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
