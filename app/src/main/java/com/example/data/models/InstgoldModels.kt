package com.example.data.models

data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val avatarResId: Int? = null,
    val avatarUrl: String? = null,
    val bio: String = "",
    val website: String = "instagold.vip/alexandra",
    val isVerified: Boolean = true,
    val isVip: Boolean = true,
    val isFollowing: Boolean = false,
    val followersCount: String = "142.8K",
    val followingCount: String = "380",
    val postsCount: String = "52",
    val isCurrentUser: Boolean = false
)

data class Comment(
    val id: String,
    val postId: String,
    val author: User,
    val text: String,
    val hindiTranslation: String = "",
    val englishTranslation: String = "",
    val isTranslated: Boolean = false,
    val currentLanguage: String = "original", // "original", "hi", "en"
    val timestamp: String = "2h ago",
    val likesCount: Int = 12,
    val isLiked: Boolean = false
)

data class Post(
    val id: String,
    val author: User,
    val mediaResId: Int? = null,
    val mediaUrl: String? = null,
    val caption: String,
    val hashtags: List<String> = emptyList(),
    val location: String? = null,
    val likesCount: Int = 1240,
    val commentsCount: Int = 86,
    val sharesCount: Int = 34,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val timestamp: String = "3h ago",
    val filterName: String = "Normal",
    val isVideo: Boolean = false,
    val comments: List<Comment> = emptyList()
)

data class Reel(
    val id: String,
    val author: User,
    val mediaResId: Int? = null,
    val mediaUrl: String? = null,
    val caption: String,
    val audioTitle: String = "Instagold Master Sound • 24K Luxury",
    val likesCount: Int = 14200,
    val commentsCount: Int = 385,
    val sharesCount: Int = 920,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val viewsCount: String = "384K"
)

data class StorySegment(
    val id: String,
    val mediaResId: Int? = null,
    val mediaUrl: String? = null,
    val caption: String = "",
    val durationSeconds: Int = 5
)

data class Story(
    val id: String,
    val author: User,
    val segments: List<StorySegment>,
    val isViewed: Boolean = false,
    val timestamp: String = "4h ago"
)

enum class NotificationType {
    LIKE, COMMENT, FOLLOW, MENTION, VIP_REWARD
}

data class InstNotification(
    val id: String,
    val type: NotificationType,
    val actor: User,
    val content: String,
    val postPreviewResId: Int? = null,
    val timestamp: String = "10m ago",
    val isRead: Boolean = false
)

data class ChatMessage(
    val id: String,
    val senderId: String,
    val text: String,
    val imageResId: Int? = null,
    val timestamp: String = "10:45 AM",
    val isFromMe: Boolean = false
)

data class Conversation(
    val id: String,
    val participant: User,
    val lastMessage: String,
    val lastMessageTime: String = "12m ago",
    val unreadCount: Int = 0,
    val isOnline: Boolean = true,
    val messages: List<ChatMessage> = emptyList()
)

data class AiCaptionSuggestion(
    val id: String,
    val tone: String,
    val caption: String,
    val hashtags: List<String>
)
