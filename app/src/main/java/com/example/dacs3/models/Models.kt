package com.example.dacs3.models

data class HomeResponse(
    val notifications: List<Notification>,
    val tasks: List<Task>,
    val workspaces: List<Workspace>,
    val directMessages: List<DirectMessage>
)

data class User(
    val id: String,
    val name: String,
    val avatar: String?,
    val status: UserStatus = UserStatus.OFFLINE
)

enum class UserStatus {
    ONLINE,
    OFFLINE,
    AWAY,
    BUSY
}

data class Notification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean = false
)

enum class NotificationType {
    TASK_ASSIGNED,
    TASK_COMPLETED,
    MESSAGE_RECEIVED,
    WORKSPACE_INVITE,
    SYSTEM
}

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val priority: TaskPriority,
    val assignee: User?,
    val dueDate: Long?,
    val workspaceId: String
)

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    REVIEW,
    DONE,
    COMPLETED
}

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

data class Workspace(
    val id: String,
    val name: String,
    val description: String,
    val type: WorkspaceType,
    val members: List<User>,
    val tasks: List<Task>,
    val isArchived: Boolean = false
)

enum class WorkspaceType {
    TEAM,
    PROJECT,
    PERSONAL
}

data class DirectMessage(
    val id: String,
    val participants: List<User>,
    val lastMessage: Message?,
    val unreadCount: Int,
    val isGroup: Boolean,
    val isArchived: Boolean = false
)

data class Message(
    val id: String,
    val content: String,
    val sender: User,
    val timestamp: Long
) 