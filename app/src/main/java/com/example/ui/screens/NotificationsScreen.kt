package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.data.models.InstNotification
import com.example.data.models.NotificationType
import com.example.ui.NotifFilterTab
import com.example.ui.theme.*

@Composable
fun NotificationsScreen(
    notifications: List<InstNotification>,
    selectedFilter: NotifFilterTab,
    onFilterChange: (NotifFilterTab) -> Unit,
    onMarkAllRead: () -> Unit,
    onNotificationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("notifications_screen")
    ) {
        // Sub-header with Mark All Read
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "ACTIVITY & ALERTS",
                color = GoldGlow,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Text(
                text = "Mark all as read",
                color = GoldPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable(onClick = onMarkAllRead)
                    .testTag("mark_all_read_button")
            )
        }

        // Filter Pills Row
        val filters = listOf(
            NotifFilterTab.ALL to "All",
            NotifFilterTab.LIKES to "Likes",
            NotifFilterTab.COMMENTS to "Comments",
            NotifFilterTab.FOLLOWS to "Follows"
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { (tab, label) ->
                val isSelected = tab == selectedFilter
                val bgMod = if (isSelected) Modifier.background(GoldenGradient) else Modifier.background(DarkSecondary)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .then(bgMod)
                        .border(
                            1.dp,
                            if (isSelected) GoldLight else DarkBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onFilterChange(tab) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("notif_filter_$label")
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) DarkBackground else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Notifications List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (notifications.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No notifications in this category.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(notifications, key = { it.id }) { notif ->
                    NotificationCard(
                        notification = notif,
                        onClick = { onNotificationClick(notif.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: InstNotification,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("notif_card_${notification.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead) DarkSecondary else DarkSurface
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            if (!notification.isRead) DarkBorderHighlight else DarkBorder.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Actor Avatar with category badge
            Box(
                modifier = Modifier.size(46.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .border(1.dp, GoldPrimary, CircleShape)
                ) {
                    if (notification.actor.avatarResId != null) {
                        Image(
                            painter = painterResource(id = notification.actor.avatarResId),
                            contentDescription = notification.actor.username,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Category Icon Badge on bottom-right of avatar
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(18.dp)
                        .background(
                            when (notification.type) {
                                NotificationType.LIKE -> LikeRed
                                NotificationType.COMMENT -> GoldPrimary
                                NotificationType.FOLLOW -> Color(0xFF3897F0)
                                NotificationType.VIP_REWARD -> GoldGlow
                                else -> GoldPrimary
                            },
                            CircleShape
                        )
                        .border(1.dp, DarkBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (notification.type) {
                            NotificationType.LIKE -> Icons.Default.Favorite
                            NotificationType.COMMENT -> Icons.Default.ChatBubble
                            NotificationType.FOLLOW -> Icons.Default.PersonAdd
                            NotificationType.VIP_REWARD -> Icons.Default.WorkspacePremium
                            else -> Icons.Default.Notifications
                        },
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = notification.actor.username,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    if (notification.actor.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "VIP",
                            tint = GoldGlow,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Text(
                    text = notification.content,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Text(
                    text = notification.timestamp,
                    color = TextTertiary,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Thumbnail or Unread Indicator
            if (notification.postPreviewResId != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = notification.postPreviewResId),
                    contentDescription = "Post thumbnail",
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, DarkBorder, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            if (!notification.isRead) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(GoldGlow, CircleShape)
                )
            }
        }
    }
}
