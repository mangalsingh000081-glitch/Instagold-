package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.Post
import com.example.data.models.Reel
import com.example.data.models.User
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    user: User,
    userPosts: List<Post>,
    userReels: List<Reel>,
    bookmarkedPosts: List<Post>,
    onEditProfileClick: () -> Unit,
    onPostClick: (Post) -> Unit,
    onReelClick: (Reel) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Posts, 1: Reels, 2: Tagged, 3: VIP Pass
    var showVipModal by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("profile_screen")
    ) {
        // Top Profile Info Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Row 1: Avatar + Stats Counters
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Profile Avatar with Glowing Gold Border
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .clip(CircleShape)
                        .background(StoryRingGradient)
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(DarkBackground)
                            .padding(2.dp)
                    ) {
                        if (user.avatarResId != null) {
                            Image(
                                painter = painterResource(id = user.avatarResId),
                                contentDescription = user.displayName,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else if (!user.avatarUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = user.displayName,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // Stats: Posts, Followers, Following
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatCounter(count = userPosts.size.toString(), label = "Posts")
                    StatCounter(count = user.followersCount, label = "Followers")
                    StatCounter(count = user.followingCount, label = "Following")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display Name & VIP Crown
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                if (user.isVerified) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "VIP Gold",
                        tint = GoldGlow,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Handle
            Text(
                text = "@${user.username}",
                color = GoldPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Bio
            Text(
                text = user.bio,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            // Website Link
            if (user.website.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        Toast.makeText(context, "Opening ${user.website} ⚜️", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = user.website,
                        color = GoldGlow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row: Edit Profile, Share Profile, VIP Pass
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEditProfileClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("edit_profile_button"),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, DarkBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DarkSecondary,
                        contentColor = TextPrimary
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Edit Profile", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Profile link copied to clipboard! ✨", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("share_profile_button"),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, DarkBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DarkSecondary,
                        contentColor = TextPrimary
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Share Profile", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                IconButton(
                    onClick = { showVipModal = !showVipModal },
                    modifier = Modifier
                        .size(36.dp)
                        .background(GoldenGradient, RoundedCornerShape(8.dp))
                        .testTag("vip_pass_icon_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = "VIP Pass",
                        tint = DarkBackground,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // VIP Gold Card Modal Showcase if open
        if (showVipModal) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                VipCardShowcase(user = user)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Tabs Row: Posts (Grid) | Reels | Tagged
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSecondary,
            contentColor = GoldGlow,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = GoldGlow,
                    height = 2.dp
                )
            },
            divider = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DarkBorder)
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                icon = {
                    Icon(
                        imageVector = Icons.Default.GridOn,
                        contentDescription = "Posts Grid",
                        tint = if (selectedTab == 0) GoldGlow else TextSecondary
                    )
                },
                modifier = Modifier.testTag("profile_tab_posts")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = "Reels",
                        tint = if (selectedTab == 1) GoldGlow else TextSecondary
                    )
                },
                modifier = Modifier.testTag("profile_tab_reels")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                icon = {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = "Tagged Posts",
                        tint = if (selectedTab == 2) GoldGlow else TextSecondary
                    )
                },
                modifier = Modifier.testTag("profile_tab_tagged")
            )
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Posts Grid (Instagram 3-Column)
                if (userPosts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No posts yet. Tap (+) to share your first luxury masterpiece.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(1.dp),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                        verticalArrangement = Arrangement.spacedBy(1.5.dp)
                    ) {
                        items(userPosts, key = { it.id }) { post ->
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clickable { onPostClick(post) }
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
                            }
                        }
                    }
                }
            }
            1 -> {
                // Reels Tab (3-Column Vertical 9:16 Video Grid with View Counts)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(1.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(userReels, key = { it.id }) { reel ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(9f / 16f)
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onReelClick(reel) }
                        ) {
                            if (reel.mediaResId != null) {
                                Image(
                                    painter = painterResource(id = reel.mediaResId),
                                    contentDescription = reel.caption,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Dark Gradient Overlay for View Count
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                        )
                                    )
                            )

                            // View Count & Play Icon
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = TextPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = reel.viewsCount,
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
            2 -> {
                // Tagged Tab
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(1.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                    verticalArrangement = Arrangement.spacedBy(1.5.dp)
                ) {
                    items(bookmarkedPosts, key = { "tagged_${it.id}" }) { post ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable { onPostClick(post) }
                        ) {
                            if (post.mediaResId != null) {
                                Image(
                                    painter = painterResource(id = post.mediaResId),
                                    contentDescription = post.caption,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCounter(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun VipCardShowcase(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSecondary),
        border = BorderStroke(1.5.dp, GoldPrimary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkGlassGradient)
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "INSTAGOLD VIP",
                        color = GoldGlow,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "VIP Crest",
                        tint = GoldGlow,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = "TIER 1 • BLACK & GOLD CONNOISSEUR",
                        color = GoldLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "4892  •  7710  •  8830  •  2026",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "MEMBER",
                            color = TextSecondary,
                            fontSize = 9.sp
                        )
                        Text(
                            text = user.displayName.uppercase(),
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "UNLIMITED PRIVILEGES",
                        color = GoldPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
