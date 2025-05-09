package com.example.dacs3.data

import com.example.dacs3.models.*
import java.util.*

object MockData {
    val mockUser = User(
        id = "1",
        name = "John Doe",
        avatar = "https://i.pravatar.cc/150?img=1",
        status = UserStatus.ONLINE
    )

    val mockUsers = listOf(
        User(
            id = "2",
            name = "Jane Smith",
            avatar = "https://i.pravatar.cc/150?img=2",
            status = UserStatus.ONLINE
        ),
        User(
            id = "3",
            name = "Mike Johnson",
            avatar = "https://i.pravatar.cc/150?img=3",
            status = UserStatus.AWAY
        ),
        User(
            id = "4",
            name = "Sarah Wilson",
            avatar = "https://i.pravatar.cc/150?img=4",
            status = UserStatus.BUSY
        )
    )

    val mockWorkspaces = listOf(
        Workspace(
            id = "1",
            name = "Project Alpha",
            description = "Main project workspace for team collaboration",
            type = WorkspaceType.PROJECT,
            members = mockUsers,
            tasks = emptyList()
        ),
        Workspace(
            id = "2",
            name = "Team Beta",
            description = "Team workspace for daily operations",
            type = WorkspaceType.TEAM,
            members = mockUsers.take(2),
            tasks = emptyList()
        )
    )

    val mockTasks = listOf(
        Task(
            id = "1",
            title = "Implement login screen",
            description = "Create a modern login screen with email and password fields",
            status = TaskStatus.IN_PROGRESS,
            priority = TaskPriority.HIGH,
            assignee = mockUser,
            dueDate = System.currentTimeMillis() + 86400000, // Tomorrow
            workspaceId = "1"
        ),
        Task(
            id = "2",
            title = "Design user profile",
            description = "Design and implement user profile screen with avatar upload",
            status = TaskStatus.TODO,
            priority = TaskPriority.MEDIUM,
            assignee = mockUsers[0],
            dueDate = System.currentTimeMillis() + 172800000, // Day after tomorrow
            workspaceId = "1"
        )
    )

    val mockMessages = listOf(
        Message(
            id = "1",
            content = "Hey, how's the project going?",
            sender = mockUser,
            timestamp = System.currentTimeMillis() - 3600000 // 1 hour ago
        ),
        Message(
            id = "2",
            content = "Great! Just finished the login screen",
            sender = mockUsers[0],
            timestamp = System.currentTimeMillis() - 1800000 // 30 minutes ago
        )
    )

    val mockDirectMessages = listOf(
        DirectMessage(
            id = "1",
            participants = listOf(mockUser, mockUsers[0]),
            lastMessage = mockMessages[0],
            unreadCount = 2,
            isGroup = false
        ),
        DirectMessage(
            id = "2",
            participants = listOf(mockUser, mockUsers[1], mockUsers[2]),
            lastMessage = mockMessages[1],
            unreadCount = 0,
            isGroup = true
        )
    )

    val mockNotifications = listOf(
        Notification(
            id = "1",
            type = NotificationType.TASK_ASSIGNED,
            title = "New Task Assigned",
            content = "You have been assigned to 'Implement login screen'",
            timestamp = System.currentTimeMillis() - 7200000 // 2 hours ago
        ),
        Notification(
            id = "2",
            type = NotificationType.MESSAGE_RECEIVED,
            title = "New Message",
            content = "Jane Smith sent you a message",
            timestamp = System.currentTimeMillis() - 3600000 // 1 hour ago
        )
    )

    val mockHomeResponse = HomeResponse(
        notifications = mockNotifications,
        tasks = mockTasks,
        workspaces = mockWorkspaces,
        directMessages = mockDirectMessages
    )
}
