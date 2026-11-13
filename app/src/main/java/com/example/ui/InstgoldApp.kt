package com.example.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.models.Post
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.DarkBackground

@Composable
fun InstgoldApp(
    viewModel: InstgoldViewModel = viewModel()
) {
    val context = LocalContext.current

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val reels by viewModel.reels.collectAsStateWithLifecycle()
    val filteredNotifications by viewModel.filteredNotifications.collectAsStateWithLifecycle()
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val bookmarkedPosts by viewModel.bookmarkedPosts.collectAsStateWithLifecycle()
    val creators = viewModel.creators

    val isDirectMessagesOpen by viewModel.isDirectMessagesOpen.collectAsStateWithLifecycle()
    val activeConversation by viewModel.activeConversation.collectAsStateWithLifecycle()

    val activeStory by viewModel.activeStory.collectAsStateWithLifecycle()
    val activeCommentsPost by viewModel.activeCommentsPost.collectAsStateWithLifecycle()
    val showEditProfile by viewModel.showEditProfile.collectAsStateWithLifecycle()
    val showAiCaptionSheet by viewModel.showAiCaptionSheet.collectAsStateWithLifecycle()

    val discoverSearchQuery by viewModel.discoverSearchQuery.collectAsStateWithLifecycle()
    val selectedTrendingTag by viewModel.selectedTrendingTag.collectAsStateWithLifecycle()
    val selectedNotifFilter by viewModel.selectedNotifFilter.collectAsStateWithLifecycle()

    val unreadNotifsCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val unreadMessagesCount by viewModel.unreadMessagesCount.collectAsStateWithLifecycle()

    var prefilledCaption by remember { mutableStateOf("") }
    var prefilledHashtags by remember { mutableStateOf<List<String>>(emptyList()) }
    var isSplashVisible by remember { mutableStateOf(true) }

    // Back handling: If in chat, back to DM list; if in DM list, back to main feed; if in sub tab, back to Home
    BackHandler(enabled = isDirectMessagesOpen || activeConversation != null || currentTab != NavigationTab.HOME) {
        when {
            activeConversation != null -> viewModel.closeConversation()
            isDirectMessagesOpen -> viewModel.closeDirectMessages()
            currentTab != NavigationTab.HOME -> viewModel.selectTab(NavigationTab.HOME)
        }
    }

    if (isSplashVisible) {
        SplashScreen(
            onDismiss = { isSplashVisible = false }
        )
    } else {
        Scaffold(
            topBar = {
                if (currentTab != NavigationTab.MESSAGES && activeConversation == null) {
                    InstgoldTopBar(
                        title = when (currentTab) {
                            NavigationTab.HOME -> null
                            NavigationTab.DISCOVER -> "Explore"
                            NavigationTab.CREATE -> "New Post"
                            NavigationTab.REELS -> "Reels"
                            NavigationTab.MESSAGES -> "Messages"
                            NavigationTab.NOTIFICATIONS -> "Activity"
                            NavigationTab.PROFILE -> currentUser.username
                        },
                        showBackButton = currentTab != NavigationTab.HOME && currentTab != NavigationTab.DISCOVER && currentTab != NavigationTab.PROFILE,
                        onBackClick = { viewModel.selectTab(NavigationTab.HOME) },
                        unreadNotifCount = unreadNotifsCount,
                        unreadMessageCount = unreadMessagesCount,
                        onNotificationsClick = { viewModel.selectTab(NavigationTab.NOTIFICATIONS) },
                        onMessagesClick = { viewModel.selectTab(NavigationTab.MESSAGES) },
                        onReelsClick = { viewModel.selectTab(NavigationTab.REELS) },
                        onLogoClick = { isSplashVisible = true }
                    )
                }
            },
            bottomBar = {
                if (activeConversation == null) {
                    InstgoldBottomNav(
                        currentTab = currentTab,
                        onTabSelected = { tab -> viewModel.selectTab(tab) },
                        unreadNotifCount = unreadNotifsCount,
                        unreadMessageCount = unreadMessagesCount
                    )
                }
            },
            containerColor = DarkBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(DarkBackground)
            ) {
                when {
                    activeConversation != null -> {
                        ChatConversationScreen(
                            conversation = activeConversation!!,
                            onSendMessage = { text ->
                                viewModel.sendMessage(activeConversation!!.id, text)
                            },
                            onBackClick = { viewModel.closeConversation() }
                        )
                    }
                    else -> {
                        when (currentTab) {
                            NavigationTab.HOME -> {
                            HomeScreen(
                                stories = stories,
                                posts = posts,
                                currentUser = currentUser,
                                onStoryClick = { story -> viewModel.openStory(story) },
                                onAddStoryClick = {
                                    viewModel.selectTab(NavigationTab.CREATE)
                                },
                                onLikePost = { postId -> viewModel.toggleLike(postId) },
                                onBookmarkPost = { postId -> viewModel.toggleBookmark(postId) },
                                onCommentPost = { post -> viewModel.openComments(post) },
                                onSharePost = { post ->
                                    Toast.makeText(context, "VIP Share link generated for ${post.author.username}'s post ⚜️", Toast.LENGTH_SHORT).show()
                                },
                                onTranslateComment = { postId, commentId ->
                                    viewModel.toggleCommentTranslate(postId, commentId)
                                },
                                onFollowClick = { userId -> viewModel.toggleFollow(userId) }
                            )
                        }

                        NavigationTab.DISCOVER -> {
                            DiscoverScreen(
                                posts = posts,
                                creators = creators,
                                searchQuery = discoverSearchQuery,
                                selectedTag = selectedTrendingTag,
                                onSearchChange = { q -> viewModel.setDiscoverSearchQuery(q) },
                                onTagSelect = { tag -> viewModel.setSelectedTrendingTag(tag) },
                                onFollowToggle = { uid -> viewModel.toggleFollow(uid) },
                                onPostClick = { post -> viewModel.openComments(post) }
                            )
                        }

                        NavigationTab.CREATE -> {
                            CreatePostScreen(
                                onPublishPost = { caption, hashtags, location, resId, url, filter ->
                                    viewModel.createPost(caption, hashtags, location, resId, url, filter)
                                    Toast.makeText(context, "Masterpiece published to Instagold feed! ✨", Toast.LENGTH_SHORT).show()
                                },
                                onOpenAiCaptions = { viewModel.openAiCaptionSheet() },
                                prefilledCaption = prefilledCaption,
                                prefilledHashtags = prefilledHashtags
                            )
                        }

                        NavigationTab.REELS -> {
                            ReelsScreen(
                                reels = reels,
                                onLikeReel = { id -> viewModel.toggleReelLike(id) },
                                onBookmarkReel = { id -> viewModel.toggleReelBookmark(id) },
                                onCommentReel = { reel ->
                                    Toast.makeText(context, "Opening comments for @${reel.author.username}", Toast.LENGTH_SHORT).show()
                                },
                                onShareReel = { reel ->
                                    Toast.makeText(context, "VIP Reel link generated ⚜️", Toast.LENGTH_SHORT).show()
                                },
                                onFollowClick = { uid -> viewModel.toggleFollow(uid) }
                            )
                        }

                        NavigationTab.MESSAGES -> {
                            DirectMessagesScreen(
                                conversations = conversations,
                                onlineCreators = creators,
                                onConversationClick = { convId -> viewModel.openConversation(convId) },
                                onBackClick = { viewModel.selectTab(NavigationTab.HOME) }
                            )
                        }

                        NavigationTab.NOTIFICATIONS -> {
                            NotificationsScreen(
                                notifications = filteredNotifications,
                                selectedFilter = selectedNotifFilter,
                                onFilterChange = { f -> viewModel.setNotificationFilter(f) },
                                onMarkAllRead = { viewModel.markAllNotificationsRead() },
                                onNotificationClick = { id -> viewModel.markNotificationRead(id) }
                            )
                        }

                        NavigationTab.PROFILE -> {
                            ProfileScreen(
                                user = currentUser,
                                userPosts = posts.filter { it.author.isCurrentUser },
                                userReels = reels,
                                bookmarkedPosts = bookmarkedPosts,
                                onEditProfileClick = { viewModel.openEditProfile() },
                                onPostClick = { post -> viewModel.openComments(post) },
                                onReelClick = { reel ->
                                    viewModel.selectTab(NavigationTab.REELS)
                                }
                            )
                        }
                    }
                }
            }

            // Modal Overlays
            // 1. Full-screen Story Viewer
            if (activeStory != null) {
                StoryViewerDialog(
                    story = activeStory!!,
                    onDismiss = { viewModel.closeStory() },
                    onSendReply = { reply ->
                        Toast.makeText(context, "Reply sent to ${activeStory!!.author.username} 🥂", Toast.LENGTH_SHORT).show()
                        viewModel.closeStory()
                    }
                )
            }

            // 2. Comments Bottom Sheet
            if (activeCommentsPost != null) {
                CommentsBottomSheet(
                    post = activeCommentsPost!!,
                    currentUser = currentUser,
                    onDismiss = { viewModel.closeComments() },
                    onAddComment = { text ->
                        viewModel.addComment(activeCommentsPost!!.id, text)
                    },
                    onTranslateComment = { commentId ->
                        viewModel.toggleCommentTranslate(activeCommentsPost!!.id, commentId)
                    }
                )
            }

            // 3. AI Caption Generator Bottom Sheet
            if (showAiCaptionSheet) {
                AiCaptionBottomSheet(
                    onDismiss = { viewModel.closeAiCaptionSheet() },
                    onSelectCaption = { caption, hashtags ->
                        prefilledCaption = caption
                        prefilledHashtags = hashtags
                        viewModel.closeAiCaptionSheet()
                        viewModel.selectTab(NavigationTab.CREATE)
                        Toast.makeText(context, "AI Caption applied! ⚜️", Toast.LENGTH_SHORT).show()
                    },
                    onGenerate = { prompt, tone ->
                        viewModel.generateAiCaptions(prompt, tone)
                    }
                )
            }

            // 4. Edit Profile Dialog
            if (showEditProfile) {
                EditProfileDialog(
                    user = currentUser,
                    onDismiss = { viewModel.closeEditProfile() },
                    onSave = { name, bio, username ->
                        viewModel.updateProfile(name, bio, username)
                        Toast.makeText(context, "Profile updated successfully! ⚜️", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
}


