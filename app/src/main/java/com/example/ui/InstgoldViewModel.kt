package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.*
import com.example.data.repository.InstgoldRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class NavigationTab {
    HOME, DISCOVER, CREATE, REELS, MESSAGES, NOTIFICATIONS, PROFILE
}

enum class NotifFilterTab {
    ALL, LIKES, COMMENTS, FOLLOWS
}

class InstgoldViewModel(
    val repository: InstgoldRepository = InstgoldRepository()
) : ViewModel() {

    val currentUser = repository.currentUser
    val stories = repository.stories
    val posts = repository.posts
    val reels = repository.reels
    val notifications = repository.notifications
    val conversations = repository.conversations
    val bookmarkedPosts = repository.bookmarkedPosts
    val creators = repository.creators

    // Navigation & Screen States
    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _isDirectMessagesOpen = MutableStateFlow(false)
    val isDirectMessagesOpen: StateFlow<Boolean> = _isDirectMessagesOpen.asStateFlow()

    private val _activeConversationId = MutableStateFlow<String?>(null)
    val activeConversationId: StateFlow<String?> = _activeConversationId.asStateFlow()

    val activeConversation: StateFlow<Conversation?> = combine(
        conversations,
        _activeConversationId
    ) { convs, id ->
        convs.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Modal dialogs & sheets
    private val _activeStory = MutableStateFlow<Story?>(null)
    val activeStory: StateFlow<Story?> = _activeStory.asStateFlow()

    private val _commentsPostId = MutableStateFlow<String?>(null)
    val commentsPostId: StateFlow<String?> = _commentsPostId.asStateFlow()

    val activeCommentsPost: StateFlow<Post?> = combine(
        posts,
        _commentsPostId
    ) { postList, id ->
        postList.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _showEditProfile = MutableStateFlow(false)
    val showEditProfile: StateFlow<Boolean> = _showEditProfile.asStateFlow()

    private val _showAiCaptionSheet = MutableStateFlow(false)
    val showAiCaptionSheet: StateFlow<Boolean> = _showAiCaptionSheet.asStateFlow()

    // Discover search and filter state
    private val _discoverSearchQuery = MutableStateFlow("")
    val discoverSearchQuery: StateFlow<String> = _discoverSearchQuery.asStateFlow()

    private val _selectedTrendingTag = MutableStateFlow("All")
    val selectedTrendingTag: StateFlow<String> = _selectedTrendingTag.asStateFlow()

    // Notification filter state
    private val _selectedNotifFilter = MutableStateFlow(NotifFilterTab.ALL)
    val selectedNotifFilter: StateFlow<NotifFilterTab> = _selectedNotifFilter.asStateFlow()

    // Filtered notifications
    val filteredNotifications: StateFlow<List<InstNotification>> = combine(
        notifications,
        _selectedNotifFilter
    ) { notifs, filter ->
        when (filter) {
            NotifFilterTab.ALL -> notifs
            NotifFilterTab.LIKES -> notifs.filter { it.type == NotificationType.LIKE }
            NotifFilterTab.COMMENTS -> notifs.filter { it.type == NotificationType.COMMENT }
            NotifFilterTab.FOLLOWS -> notifs.filter { it.type == NotificationType.FOLLOW || it.type == NotificationType.VIP_REWARD }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val unreadMessagesCount: StateFlow<Int> = conversations.map { list ->
        list.sumOf { it.unreadCount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // User interactions
    fun selectTab(tab: NavigationTab) {
        _isDirectMessagesOpen.value = (tab == NavigationTab.MESSAGES)
        _activeConversationId.value = null
        _currentTab.value = tab
    }

    fun openDirectMessages() {
        _activeConversationId.value = null
        _currentTab.value = NavigationTab.MESSAGES
        _isDirectMessagesOpen.value = true
    }

    fun closeDirectMessages() {
        _isDirectMessagesOpen.value = false
        _activeConversationId.value = null
        _currentTab.value = NavigationTab.HOME
    }

    fun openConversation(convId: String) {
        _activeConversationId.value = convId
    }

    fun closeConversation() {
        _activeConversationId.value = null
    }

    fun openStory(story: Story) {
        _activeStory.value = story
        repository.markStoryViewed(story.id)
    }

    fun closeStory() {
        _activeStory.value = null
    }

    fun openComments(post: Post) {
        _commentsPostId.value = post.id
    }

    fun closeComments() {
        _commentsPostId.value = null
    }

    fun openEditProfile() {
        _showEditProfile.value = true
    }

    fun closeEditProfile() {
        _showEditProfile.value = false
    }

    fun openAiCaptionSheet() {
        _showAiCaptionSheet.value = true
    }

    fun closeAiCaptionSheet() {
        _showAiCaptionSheet.value = false
    }

    fun setDiscoverSearchQuery(query: String) {
        _discoverSearchQuery.value = query
    }

    fun setSelectedTrendingTag(tag: String) {
        _selectedTrendingTag.value = tag
    }

    fun setNotificationFilter(filter: NotifFilterTab) {
        _selectedNotifFilter.value = filter
    }

    // Repository Actions
    fun toggleLike(postId: String) = repository.toggleLike(postId)
    fun toggleBookmark(postId: String) = repository.toggleBookmark(postId)
    fun toggleReelLike(reelId: String) = repository.toggleReelLike(reelId)
    fun toggleReelBookmark(reelId: String) = repository.toggleReelBookmark(reelId)
    fun addComment(postId: String, text: String) = repository.addComment(postId, text)
    fun toggleCommentTranslate(postId: String, commentId: String) = repository.toggleCommentTranslate(postId, commentId)
    fun toggleFollow(userId: String) = repository.toggleFollow(userId)
    fun markAllNotificationsRead() = repository.markAllNotificationsAsRead()
    fun markNotificationRead(id: String) = repository.markNotificationAsRead(id)
    fun sendMessage(convId: String, text: String) = repository.sendMessage(convId, text)
    fun updateProfile(name: String, bio: String, username: String) {
        repository.updateProfile(name, bio, username)
        _showEditProfile.value = false
    }

    fun createPost(caption: String, hashtags: List<String>, location: String?, mediaResId: Int?, mediaUrl: String?, filterName: String) {
        repository.createPost(caption, hashtags, location, mediaResId, mediaUrl, filterName)
        _currentTab.value = NavigationTab.HOME
    }

    fun generateAiCaptions(prompt: String, tone: String): List<AiCaptionSuggestion> {
        return repository.generateAiCaptions(prompt, tone)
    }
}
