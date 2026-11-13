package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.models.Post
import com.example.data.models.Story
import com.example.data.models.User
import com.example.ui.components.PostCard
import com.example.ui.components.StoryTray
import com.example.ui.theme.DarkBackground

@Composable
fun HomeScreen(
    stories: List<Story>,
    posts: List<Post>,
    currentUser: User,
    onStoryClick: (Story) -> Unit,
    onAddStoryClick: () -> Unit,
    onLikePost: (String) -> Unit,
    onBookmarkPost: (String) -> Unit,
    onCommentPost: (Post) -> Unit,
    onSharePost: (Post) -> Unit,
    onTranslateComment: (postId: String, commentId: String) -> Unit,
    onFollowClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("home_feed_list"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Stories Tray
        item {
            StoryTray(
                stories = stories,
                currentUser = currentUser,
                onStoryClick = onStoryClick,
                onAddStoryClick = onAddStoryClick
            )
        }

        // 2. Feed Posts
        items(posts, key = { it.id }) { post ->
            PostCard(
                post = post,
                onLikeClick = { onLikePost(post.id) },
                onBookmarkClick = { onBookmarkPost(post.id) },
                onCommentClick = { onCommentPost(post) },
                onShareClick = { onSharePost(post) },
                onTranslateComment = { commentId ->
                    onTranslateComment(post.id, commentId)
                },
                onFollowClick = { onFollowClick(post.author.id) }
            )
        }
    }
}
