package com.example.data.repository

import com.example.R
import com.example.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InstgoldRepository {

    // Current Authenticated User
    private val _currentUser = MutableStateFlow(
        User(
            id = "user_me",
            username = "alexandra_royce",
            displayName = "Alexandra Royce",
            avatarResId = R.drawable.ic_instgold_logo,
            bio = "Curating timeless elegance. Private collector & luxury connoisseur. ⚜️ Monaco • Zurich • Dubai",
            isVerified = true,
            isVip = true,
            followersCount = "142.8K",
            followingCount = "380",
            postsCount = "52",
            isCurrentUser = true
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // VIP Creators List for Discover / Stories / Suggestions
    val creators = listOf(
        User(
            id = "u1",
            username = "julian_vance",
            displayName = "Julian Vance",
            avatarResId = R.drawable.luxury_watch_1794371198720,
            bio = "Horology curator & vintage timepiece archivist. Geneva, Switzerland.",
            isVerified = true,
            isVip = true,
            isFollowing = true,
            followersCount = "289K",
            followingCount = "215",
            postsCount = "120"
        ),
        User(
            id = "u2",
            username = "aurora_monaco",
            displayName = "Aurora de Valois",
            avatarResId = R.drawable.luxury_yacht_1794371181183,
            bio = "High Seas & Haute Couture. Yachting lifestyle ambassador.",
            isVerified = true,
            isVip = true,
            isFollowing = false,
            followersCount = "510K",
            followingCount = "190",
            postsCount = "340"
        ),
        User(
            id = "u3",
            username = "marcus_sterling",
            displayName = "Marcus Sterling",
            avatarResId = R.drawable.luxury_hypercar_1794371217978,
            bio = "Hypercar architect & track collector. Speed meets gold carbon.",
            isVerified = true,
            isVip = true,
            isFollowing = true,
            followersCount = "420K",
            followingCount = "310",
            postsCount = "95"
        ),
        User(
            id = "u4",
            username = "geneva_vault",
            displayName = "The Geneva Vault",
            avatarResId = R.drawable.ic_instgold_logo,
            bio = "Exclusive private auctions of fine art, jewels & rare assets.",
            isVerified = true,
            isVip = true,
            isFollowing = false,
            followersCount = "890K",
            followingCount = "64",
            postsCount = "410"
        )
    )

    // Stories Flow
    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    // Posts Feed Flow
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    // Notifications Flow
    private val _notifications = MutableStateFlow<List<InstNotification>>(emptyList())
    val notifications: StateFlow<List<InstNotification>> = _notifications.asStateFlow()

    // Conversations Flow
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    // Saved/Bookmarked Posts
    private val _bookmarkedPosts = MutableStateFlow<List<Post>>(emptyList())
    val bookmarkedPosts: StateFlow<List<Post>> = _bookmarkedPosts.asStateFlow()

    // Reels Flow
    private val _reels = MutableStateFlow<List<Reel>>(emptyList())
    val reels: StateFlow<List<Reel>> = _reels.asStateFlow()

    init {
        initSampleData()
    }

    private fun initSampleData() {
        val u1 = creators[0]
        val u2 = creators[1]
        val u3 = creators[2]
        val u4 = creators[3]
        val me = _currentUser.value

        // Stories
        val initialStories = listOf(
            Story(
                id = "story_me",
                author = me,
                segments = listOf(
                    StorySegment(
                        id = "seg_me_1",
                        mediaResId = R.drawable.luxury_yacht_1794371181183,
                        caption = "Boarding for Portofino sunset regatta 🥂✨"
                    )
                ),
                isViewed = false,
                timestamp = "Just now"
            ),
            Story(
                id = "story_1",
                author = u1,
                segments = listOf(
                    StorySegment(
                        id = "seg_1_1",
                        mediaResId = R.drawable.luxury_watch_1794371198720,
                        caption = "Inspecting 1-of-1 tourbillon movement today in Geneva. ⚜️"
                    ),
                    StorySegment(
                        id = "seg_1_2",
                        mediaResId = R.drawable.ic_instgold_logo,
                        caption = "Private salon preview open to Instagold VIPs tonight."
                    )
                ),
                isViewed = false,
                timestamp = "1h ago"
            ),
            Story(
                id = "story_2",
                author = u2,
                segments = listOf(
                    StorySegment(
                        id = "seg_2_1",
                        mediaResId = R.drawable.luxury_yacht_1794371181183,
                        caption = "Sunset over Mediterranean waters. Serenity at its highest altitude."
                    )
                ),
                isViewed = false,
                timestamp = "3h ago"
            ),
            Story(
                id = "story_3",
                author = u3,
                segments = listOf(
                    StorySegment(
                        id = "seg_3_1",
                        mediaResId = R.drawable.luxury_hypercar_1794371217978,
                        caption = "1500 BHP draped in forged 24K gold carbon weave. Track ready."
                    )
                ),
                isViewed = true,
                timestamp = "5h ago"
            ),
            Story(
                id = "story_4",
                author = u4,
                segments = listOf(
                    StorySegment(
                        id = "seg_4_1",
                        mediaResId = R.drawable.ic_instgold_logo,
                        caption = "Exclusive winter catalogue unlocked."
                    )
                ),
                isViewed = true,
                timestamp = "8h ago"
            )
        )
        _stories.value = initialStories

        // Initial Posts
        val initialPosts = listOf(
            Post(
                id = "p1",
                author = u1,
                mediaResId = R.drawable.luxury_watch_1794371198720,
                caption = "The crown jewel of Swiss horology. 18-karat solid yellow gold skeletonized bridge with manual winding tourbillon mechanism. Every gear polished by hand over 400 meticulous hours. A tribute to eternal craftsmanship.",
                hashtags = listOf("#InstagoldHorology", "#GoldStandard", "#Tourbillon", "#SwissMade", "#HauteHorlogerie"),
                location = "Geneva Salon Privé, Switzerland",
                likesCount = 3420,
                commentsCount = 142,
                sharesCount = 89,
                isLiked = true,
                isBookmarked = true,
                timestamp = "2 hours ago",
                comments = listOf(
                    Comment(
                        id = "c1_1",
                        postId = "p1",
                        author = u2,
                        text = "Magnificent luxury piece. The craftsmanship is pure perfection.",
                        hindiTranslation = "अद्भुत लक्जरी कृति। इसकी शिल्पकारी बिल्कुल त्रुटिहीन और बेजोड़ है।",
                        englishTranslation = "Magnificent luxury piece. The craftsmanship is pure perfection.",
                        timestamp = "1h ago",
                        likesCount = 28
                    ),
                    Comment(
                        id = "c1_2",
                        postId = "p1",
                        author = u3,
                        text = "The yellow gold skeleton work is unbelievable. Truly one of one.",
                        hindiTranslation = "येलो गोल्ड का यह स्केलेटन वर्क अविश्वसनीय है। वाकई अद्वितीय।",
                        englishTranslation = "The yellow gold skeleton work is unbelievable. Truly one of one.",
                        timestamp = "45m ago",
                        likesCount = 19
                    )
                )
            ),
            Post(
                id = "p2",
                author = u2,
                mediaResId = R.drawable.luxury_yacht_1794371181183,
                caption = "Chasing horizons along the Amalfi coast as the golden hour turns the sea into liquid amber. Celebrating the preview of our private charter collection with close friends.",
                hashtags = listOf("#YachtLife", "#MonacoYachtClub", "#GoldenHour", "#AmalfiCoast", "#InstagoldExclusive"),
                location = "Positano Coastline, Italy",
                likesCount = 4890,
                commentsCount = 210,
                sharesCount = 140,
                isLiked = false,
                isBookmarked = false,
                timestamp = "5 hours ago",
                comments = listOf(
                    Comment(
                        id = "c2_1",
                        postId = "p2",
                        author = me,
                        text = "Nothing matches that Mediterranean glow. Wish I joined the deck party!",
                        hindiTranslation = "उस भूमध्यसागरीय चमक की कोई तुलना नहीं। काश मैं भी डेक पार्टी में शामिल होता!",
                        englishTranslation = "Nothing matches that Mediterranean glow. Wish I joined the deck party!",
                        timestamp = "3h ago",
                        likesCount = 35
                    ),
                    Comment(
                        id = "c2_2",
                        postId = "p2",
                        author = u4,
                        text = "The golden sunset reflection against the hull is breathtaking.",
                        hindiTranslation = "जहाज के पतवार पर सुनहरे सूर्यास्त का प्रतिबिंब अत्यंत लुभावना है।",
                        englishTranslation = "The golden sunset reflection against the hull is breathtaking.",
                        timestamp = "2h ago",
                        likesCount = 14
                    )
                )
            ),
            Post(
                id = "p3",
                author = u3,
                mediaResId = R.drawable.luxury_hypercar_1794371217978,
                caption = "Conquering physics with elegance. Custom tailored forged-gold carbon aerodynamic chassis paired with twin-turbocharged V8 fury. Limited to only three bespoke editions worldwide.",
                hashtags = listOf("#Hypercar", "#GoldCarbon", "#TrackBeast", "#BespokeAuto", "#InstagoldGarage"),
                location = "Pavilion One, Dubai",
                likesCount = 6120,
                commentsCount = 385,
                sharesCount = 420,
                isLiked = true,
                isBookmarked = false,
                timestamp = "8 hours ago",
                comments = listOf(
                    Comment(
                        id = "c3_1",
                        postId = "p3",
                        author = u1,
                        text = "Peak aerodynamic design. The forged gold finish is astonishing.",
                        hindiTranslation = "उत्कृष्ट वायुगतिकीय डिज़ाइन। फोर्ज्ड गोल्ड फिनिश वाकई हैरान करने वाला है।",
                        englishTranslation = "Peak aerodynamic design. The forged gold finish is astonishing.",
                        timestamp = "6h ago",
                        likesCount = 41
                    )
                )
            )
        )
        _posts.value = initialPosts
        _bookmarkedPosts.value = initialPosts.filter { it.isBookmarked }

        // Notifications
        _notifications.value = listOf(
            InstNotification(
                id = "n1",
                type = NotificationType.LIKE,
                actor = u1,
                content = "liked your post 'Boarding for Portofino regatta'",
                postPreviewResId = R.drawable.luxury_yacht_1794371181183,
                timestamp = "8m ago",
                isRead = false
            ),
            InstNotification(
                id = "n2",
                type = NotificationType.COMMENT,
                actor = u2,
                content = "commented: 'Magnificent luxury piece. Pure perfection.'",
                postPreviewResId = R.drawable.luxury_watch_1794371198720,
                timestamp = "35m ago",
                isRead = false
            ),
            InstNotification(
                id = "n3",
                type = NotificationType.FOLLOW,
                actor = u3,
                content = "started following you.",
                timestamp = "2h ago",
                isRead = true
            ),
            InstNotification(
                id = "n4",
                type = NotificationType.VIP_REWARD,
                actor = u4,
                content = "verified your VIP Gold Creator Status with crown badge.",
                timestamp = "1d ago",
                isRead = true
            )
        )

        // Conversations
        _conversations.value = listOf(
            Conversation(
                id = "conv_1",
                participant = u1,
                lastMessage = "The private Geneva catalogue just got dispatched to you.",
                lastMessageTime = "15m ago",
                unreadCount = 2,
                isOnline = true,
                messages = listOf(
                    ChatMessage("m1_1", u1.id, "Good afternoon Alexandra. Are you attending the private Geneva salon tomorrow?", timestamp = "11:20 AM", isFromMe = false),
                    ChatMessage("m1_2", me.id, "Yes Julian! Flying in tonight. Would love to view the skeleton timepiece in person.", timestamp = "11:35 AM", isFromMe = true),
                    ChatMessage("m1_3", u1.id, "The private Geneva catalogue just got dispatched to you.", timestamp = "11:42 AM", isFromMe = false)
                )
            ),
            Conversation(
                id = "conv_2",
                participant = u2,
                lastMessage = "Can't wait to see your new photo series! ⚜️",
                lastMessageTime = "2h ago",
                unreadCount = 0,
                isOnline = true,
                messages = listOf(
                    ChatMessage("m2_1", u2.id, "Loved the regatta shots on your story!", timestamp = "9:10 AM", isFromMe = false),
                    ChatMessage("m2_2", me.id, "Thank you Aurora! The lighting was pure gold.", timestamp = "9:25 AM", isFromMe = true),
                    ChatMessage("m2_3", u2.id, "Can't wait to see your new photo series! ⚜️", timestamp = "9:30 AM", isFromMe = false)
                )
            ),
            Conversation(
                id = "conv_3",
                participant = u3,
                lastMessage = "Dyno numbers reached 1520 HP today.",
                lastMessageTime = "Yesterday",
                unreadCount = 0,
                isOnline = false,
                messages = listOf(
                    ChatMessage("m3_1", u3.id, "Dyno numbers reached 1520 HP today.", timestamp = "Yesterday", isFromMe = false)
                )
            )
        )

        // Reels Initial Feed
        _reels.value = listOf(
            Reel(
                id = "r1",
                author = u1,
                mediaResId = R.drawable.luxury_watch_1794371198720,
                caption = "The heartbeat of Swiss mastery. 400 hours of hand-beveling on the tourbillon skeleton bridge. ⚜️ Listen to that escapement pulse.",
                audioTitle = "Julian Vance • Geneva Escapement Rhythm 4Hz",
                likesCount = 28400,
                commentsCount = 642,
                sharesCount = 1280,
                isLiked = true,
                isBookmarked = true,
                viewsCount = "480K"
            ),
            Reel(
                id = "r2",
                author = u2,
                mediaResId = R.drawable.luxury_yacht_1794371181183,
                caption = "Liquid gold horizons off Positano cliffs. Private charter sunset regatta. 🌅🥂 #YachtLife #Instagold",
                audioTitle = "Aurora Monaco • Mediterranean Golden Hour",
                likesCount = 45900,
                commentsCount = 890,
                sharesCount = 3120,
                isLiked = false,
                isBookmarked = false,
                viewsCount = "890K"
            ),
            Reel(
                id = "r3",
                author = u3,
                mediaResId = R.drawable.luxury_hypercar_1794371217978,
                caption = "1,500 horsepower draped in forged 24K gold carbon. Dyno roar echoed across Dubai pavilion. 🔥🏁",
                audioTitle = "Marcus Sterling • Twin Turbo V8 Symphony",
                likesCount = 68200,
                commentsCount = 1420,
                sharesCount = 5400,
                isLiked = true,
                isBookmarked = false,
                viewsCount = "1.4M"
            ),
            Reel(
                id = "r4",
                author = me,
                mediaResId = R.drawable.ic_instgold_logo,
                caption = "The gold standard of social media. Welcome to the private circle of Instagold. ⚜️👑",
                audioTitle = "Instagold Anthem • The Gold Standard Original",
                likesCount = 94100,
                commentsCount = 2890,
                sharesCount = 9800,
                isLiked = false,
                isBookmarked = true,
                viewsCount = "2.8M"
            )
        )
    }

    // Reel Actions
    fun toggleReelLike(reelId: String) {
        _reels.update { list ->
            list.map { reel ->
                if (reel.id == reelId) {
                    val newLiked = !reel.isLiked
                    val newCount = if (newLiked) reel.likesCount + 1 else reel.likesCount - 1
                    reel.copy(isLiked = newLiked, likesCount = maxOf(0, newCount))
                } else reel
            }
        }
    }

    fun toggleReelBookmark(reelId: String) {
        _reels.update { list ->
            list.map { reel ->
                if (reel.id == reelId) reel.copy(isBookmarked = !reel.isBookmarked) else reel
            }
        }
    }

    // Post Actions
    fun toggleLike(postId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val newLiked = !post.isLiked
                    val newCount = if (newLiked) post.likesCount + 1 else post.likesCount - 1
                    post.copy(isLiked = newLiked, likesCount = maxOf(0, newCount))
                } else post
            }
        }
    }

    fun toggleBookmark(postId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val newSaved = !post.isBookmarked
                    post.copy(isBookmarked = newSaved)
                } else post
            }
        }
        _bookmarkedPosts.value = _posts.value.filter { it.isBookmarked }
    }

    fun addComment(postId: String, text: String) {
        val me = _currentUser.value
        val newComment = Comment(
            id = "c_${System.currentTimeMillis()}",
            postId = postId,
            author = me,
            text = text,
            hindiTranslation = "यह बहुत ही शानदार और खूबसूरत है! ⚜️",
            englishTranslation = text,
            timestamp = "Just now"
        )
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    post.copy(
                        commentsCount = post.commentsCount + 1,
                        comments = listOf(newComment) + post.comments
                    )
                } else post
            }
        }
    }

    fun toggleCommentTranslate(postId: String, commentId: String) {
        _posts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val updatedComments = post.comments.map { c ->
                        if (c.id == commentId) {
                            val nextLang = when (c.currentLanguage) {
                                "original" -> "hi"
                                "hi" -> "en"
                                else -> "original"
                            }
                            c.copy(
                                isTranslated = (nextLang != "original"),
                                currentLanguage = nextLang
                            )
                        } else c
                    }
                    post.copy(comments = updatedComments)
                } else post
            }
        }
    }

    fun toggleFollow(userId: String) {
        // Toggle follow in creators list
        // and update following counters
        val user = creators.find { it.id == userId } ?: return
        val newFollowing = !user.isFollowing
        // Update posts authored by this user
        _posts.update { list ->
            list.map { post ->
                if (post.author.id == userId) {
                    post.copy(author = post.author.copy(isFollowing = newFollowing))
                } else post
            }
        }
    }

    fun markStoryViewed(storyId: String) {
        _stories.update { list ->
            list.map { story ->
                if (story.id == storyId) story.copy(isViewed = true) else story
            }
        }
    }

    fun markNotificationAsRead(id: String) {
        _notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.update { list ->
            list.map { it.copy(isRead = true) }
        }
    }

    fun createPost(
        caption: String,
        hashtags: List<String>,
        location: String?,
        mediaResId: Int?,
        mediaUrl: String?,
        filterName: String = "Normal"
    ) {
        val me = _currentUser.value
        val newPost = Post(
            id = "p_${System.currentTimeMillis()}",
            author = me,
            mediaResId = mediaResId ?: R.drawable.ic_instgold_logo,
            mediaUrl = mediaUrl,
            caption = caption,
            hashtags = hashtags,
            location = location ?: "Exclusive Sanctuary",
            likesCount = 1,
            commentsCount = 0,
            sharesCount = 0,
            isLiked = false,
            isBookmarked = false,
            timestamp = "Just now",
            filterName = filterName
        )
        _posts.update { listOf(newPost) + it }
    }

    fun sendMessage(conversationId: String, text: String) {
        val me = _currentUser.value
        val newMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderId = me.id,
            text = text,
            timestamp = "Just now",
            isFromMe = true
        )
        _conversations.update { list ->
            list.map { conv ->
                if (conv.id == conversationId) {
                    conv.copy(
                        lastMessage = text,
                        lastMessageTime = "Just now",
                        messages = conv.messages + newMsg
                    )
                } else conv
            }
        }
    }

    fun updateProfile(displayName: String, bio: String, username: String) {
        _currentUser.update {
            it.copy(
                displayName = displayName,
                bio = bio,
                username = username
            )
        }
    }

    // AI Caption Generator Engine
    fun generateAiCaptions(prompt: String, tone: String): List<AiCaptionSuggestion> {
        val baseTopic = if (prompt.isBlank()) "timeless luxury and golden lifestyle" else prompt.trim()
        return when (tone.lowercase()) {
            "savage", "savage & bold" -> listOf(
                AiCaptionSuggestion(
                    id = "s1",
                    tone = "Savage & Bold",
                    caption = "They want the crown, but they can't carry the weight. Gold doesn't tarnish, and neither do I. ⚜️👑",
                    hashtags = listOf("#Untouchable", "#GoldStandard", "#NoCompetition", "#ApexMindset", "#Instagold")
                ),
                AiCaptionSuggestion(
                    id = "s2",
                    tone = "Savage & Bold",
                    caption = "Quiet luxury speaks louder than noisy crowds. Watching from the highest penthouse suite. 🍸",
                    hashtags = listOf("#LevelsAbove", "#SilentPower", "#BlackAndGold", "#OnlyVip")
                )
            )
            "minimalist", "minimalist aesthetic" -> listOf(
                AiCaptionSuggestion(
                    id = "m1",
                    tone = "Minimalist",
                    caption = "Understated luxury. Pure precision in $baseTopic. ⚜️",
                    hashtags = listOf("#Minimalism", "#DesignPurity", "#OnyxAndGold", "#Aesthetic")
                ),
                AiCaptionSuggestion(
                    id = "m2",
                    tone = "Minimalist",
                    caption = "Details that matter. Silence is the ultimate luxury.",
                    hashtags = listOf("#QuietLuxury", "#InstagoldCurated", "#ArtOfLiving")
                )
            )
            "poetic", "poetic elegance" -> listOf(
                AiCaptionSuggestion(
                    id = "p1",
                    tone = "Poetic Elegance",
                    caption = "Where golden light kisses obsidian shadows, moments turn into eternal heirlooms. 🌅✨",
                    hashtags = listOf("#GoldenHour", "#TimelessElegance", "#PoetryInMotion", "#HauteLiving")
                ),
                AiCaptionSuggestion(
                    id = "p2",
                    tone = "Poetic Elegance",
                    caption = "Crafted by passion, perfected by time. A symphony written in 24-karat gold.",
                    hashtags = listOf("#EternalCraft", "#GoldAura", "#RefinedTaste", "#Instagold")
                )
            )
            else -> listOf(
                AiCaptionSuggestion(
                    id = "r1",
                    tone = "Royal Luxury",
                    caption = "Elevating the standard of $baseTopic. Unrivaled perfection for those who appreciate the extraordinary. ⚜️💎",
                    hashtags = listOf("#RoyalLuxury", "#InstagoldExclusive", "#HighSociety", "#Opulence", "#VipCircle")
                ),
                AiCaptionSuggestion(
                    id = "r2",
                    tone = "Royal Luxury",
                    caption = "Living on the golden spectrum. Exclusivity isn't a choice; it's a legacy.",
                    hashtags = listOf("#BespokeLiving", "#LuxuryReimagined", "#PrivateReserve", "#GoldGlow")
                ),
                AiCaptionSuggestion(
                    id = "r3",
                    tone = "Royal Luxury",
                    caption = "Some chase trends. We set the eternal gold standard. 🏆",
                    hashtags = listOf("#GoldStandard", "#Masterpiece", "#ExclusiveAccess", "#Instagold")
                )
            )
        }
    }
}
