package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.automirrored.filled.Send
import com.example.ui.NavigationTab
import com.example.ui.theme.*

@Composable
fun InstgoldBottomNav(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    unreadNotifCount: Int = 0,
    unreadMessageCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkSecondary,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Subtle top gold accent line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DarkBorder)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // 1. Home
                BottomNavItem(
                    label = "Home",
                    iconSelected = Icons.Filled.Home,
                    iconUnselected = Icons.Outlined.Home,
                    isSelected = currentTab == NavigationTab.HOME,
                    testTag = "nav_home",
                    onClick = { onTabSelected(NavigationTab.HOME) }
                )

                // 2. Discover
                BottomNavItem(
                    label = "Discover",
                    iconSelected = Icons.Filled.Explore,
                    iconUnselected = Icons.Outlined.Explore,
                    isSelected = currentTab == NavigationTab.DISCOVER,
                    testTag = "nav_discover",
                    onClick = { onTabSelected(NavigationTab.DISCOVER) }
                )

                // 3. Center Elevated Create (+) Button with Glowing Gold Gradient
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .offset(y = (-6).dp)
                        .shadow(
                            elevation = 10.dp,
                            shape = CircleShape,
                            ambientColor = GoldGlow,
                            spotColor = GoldPrimary
                        )
                        .clip(CircleShape)
                        .background(GoldenGradient)
                        .border(1.5.dp, GoldLight, CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = DarkBackground),
                            onClick = { onTabSelected(NavigationTab.CREATE) }
                        )
                        .testTag("nav_create"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Create Post",
                        tint = DarkBackground,
                        modifier = Modifier.size(30.dp)
                    )
                }

                // 4. Messages (Moved to bottom navigation with unread badge)
                BottomNavItem(
                    label = "Messages",
                    iconSelected = Icons.AutoMirrored.Filled.Send,
                    iconUnselected = Icons.AutoMirrored.Filled.Send,
                    isSelected = currentTab == NavigationTab.MESSAGES,
                    testTag = "nav_messages",
                    badgeCount = unreadMessageCount,
                    onClick = { onTabSelected(NavigationTab.MESSAGES) }
                )

                // 5. Profile
                BottomNavItem(
                    label = "Profile",
                    iconSelected = Icons.Filled.Person,
                    iconUnselected = Icons.Outlined.Person,
                    isSelected = currentTab == NavigationTab.PROFILE,
                    testTag = "nav_profile",
                    onClick = { onTabSelected(NavigationTab.PROFILE) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    iconSelected: ImageVector,
    iconUnselected: ImageVector,
    isSelected: Boolean,
    testTag: String,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) GoldGlow else TextSecondary,
        label = "iconColor"
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1.0f,
        label = "iconScale"
    )

    Box(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = GoldPrimary.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .testTag(testTag)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                Icon(
                    imageVector = if (isSelected) iconSelected else iconUnselected,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(scale)
                )

                if (badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = (-4).dp)
                            .size(14.dp)
                            .background(GoldGlow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                            color = DarkBackground,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Micro Gold Active Pill
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .width(14.dp)
                        .height(2.5.dp)
                        .clip(CircleShape)
                        .background(GoldGlow)
                )
            } else {
                Spacer(modifier = Modifier.height(2.5.dp))
            }
        }
    }
}
