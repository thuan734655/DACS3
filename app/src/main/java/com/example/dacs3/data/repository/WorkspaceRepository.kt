package com.example.dacs3.data.repository

import com.example.dacs3.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkspaceRepository @Inject constructor(
    private val userDao: UserDao,
    private val accountDao: AccountDao,
    private val workspaceDao: WorkspaceDao,
    private val channelDao: ChannelDao,
    private val messageDao: MessageDao,
    private val taskDao: TaskDao,
    private val bugDao: BugDao,
    private val epicDao: EpicDao,
    private val notificationDao: NotificationDao,
    private val invitationDao: InvitationDao,
    private val userChannelMembershipDao: UserChannelMembershipDao,
    private val workspaceUserMembershipDao: WorkspaceUserMembershipDao
) {
    // User related
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
    
    suspend fun getUserById(userId: String): UserEntity? = userDao.getUserById(userId)
    
    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    
    suspend fun insertUsers(users: List<UserEntity>) = userDao.insertUsers(users)
    
    // Account related
    fun getAllAccounts(): Flow<List<AccountEntity>> = accountDao.getAllAccounts()
    
    suspend fun getAccountByUserId(userId: String): AccountEntity? = accountDao.getAccountByUserId(userId)
    
    suspend fun insertAccount(account: AccountEntity) = accountDao.insertAccount(account)
    
    // Workspace related
    fun getAllWorkspaces(): Flow<List<WorkspaceEntity>> = workspaceDao.getAllWorkspaces()
    
    suspend fun getWorkspaceById(workspaceId: String): WorkspaceEntity? = workspaceDao.getWorkspaceById(workspaceId)
    
    fun getWorkspacesByMember(userId: String): Flow<List<WorkspaceEntity>> = workspaceDao.getWorkspacesByMember(userId)
    
    suspend fun insertWorkspace(workspace: WorkspaceEntity) = workspaceDao.insertWorkspace(workspace)
    
    // Channel related
    fun getAllChannels(): Flow<List<ChannelEntity>> = channelDao.getAllChannels()
    
    suspend fun getChannelById(channelId: String): ChannelEntity? = channelDao.getChannelById(channelId)
    
    suspend fun insertChannel(channel: ChannelEntity) = channelDao.insertChannel(channel)
    
    suspend fun insertChannels(channels: List<ChannelEntity>) = channelDao.insertChannels(channels)
    
    // Message related
    fun getChannelMessages(channelId: String): Flow<List<MessageEntity>> = 
        messageDao.getChannelMessages(channelId)
    
    fun getDirectMessages(userId1: String, userId2: String): Flow<List<MessageEntity>> = 
        messageDao.getDirectMessages(userId1, userId2)
    
    fun getUnreadMessages(userId: String): Flow<List<MessageEntity>> = 
        messageDao.getUnreadMessages(userId)
    
    suspend fun insertMessage(message: MessageEntity) = messageDao.insertMessage(message)
    
    suspend fun insertMessages(messages: List<MessageEntity>) = messageDao.insertMessages(messages)
    
    // Task related
    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()
    
    fun getUserTasks(userId: String): Flow<List<TaskEntity>> = taskDao.getUserTasks(userId)
    
    suspend fun insertTask(task: TaskEntity) = taskDao.insertTask(task)
    
    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)
    
    // Bug related
    fun getAllBugs(): Flow<List<BugEntity>> = bugDao.getAllBugs()
    
    fun getBugsByTaskId(taskId: String): Flow<List<BugEntity>> = bugDao.getBugsByTaskId(taskId)
    
    suspend fun insertBug(bug: BugEntity) = bugDao.insertBug(bug)
    
    // Epic related
    fun getAllEpics(): Flow<List<EpicEntity>> = epicDao.getAllEpics()
    
    fun getEpicsByWorkspace(workspaceId: String): Flow<List<EpicEntity>> = epicDao.getEpicsByWorkspace(workspaceId)
    
    suspend fun insertEpic(epic: EpicEntity) = epicDao.insertEpic(epic)
    
    // Notification related
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>> = 
        notificationDao.getNotificationsForUser(userId)
    
    suspend fun insertNotification(notification: NotificationEntity) = 
        notificationDao.insertNotification(notification)
    
    // Invitation related
    fun getInvitationsForUser(userId: String): Flow<List<InvitationEntity>> = 
        invitationDao.getInvitationsForUser(userId)
    
    suspend fun insertInvitation(invitation: InvitationEntity) = 
        invitationDao.insertInvitation(invitation)
    
    // UserChannel related
    fun getUserChannelMemberships(userId: String): Flow<List<UserChannelMembership>> = 
        userChannelMembershipDao.getUserChannelMemberships(userId)
    
    suspend fun insertUserChannelMembership(membership: UserChannelMembership) = 
        userChannelMembershipDao.insertMembership(membership)
    
    // WorkspaceUser related
    fun getUserWorkspaceMemberships(userId: String): Flow<List<WorkspaceUserMembership>> = 
        workspaceUserMembershipDao.getUserWorkspaceMemberships(userId)
    
    suspend fun insertWorkspaceUserMembership(membership: WorkspaceUserMembership) = 
        workspaceUserMembershipDao.insertMembership(membership)
    
    // Helper methods for seeding data
    suspend fun seedInitialData() {
        // Only seed if the database is empty
        if (userDao.getUserCount() == 0) {
            // Seed users
            val users = listOf(
                UserEntity(
                    userId = "user1",
                    username = "Joshitha",
                    avatarUrl = null,
                    isOnline = true
                ),
                UserEntity(
                    userId = "user2",
                    username = "Ali Sarraf",
                    avatarUrl = null,
                    isOnline = true
                ),
                UserEntity(
                    userId = "user3",
                    username = "Sam Wilson",
                    avatarUrl = null,
                    isOnline = false
                ),
                UserEntity(
                    userId = "user4",
                    username = "Emily Chen",
                    avatarUrl = null,
                    isOnline = true
                )
            )
            userDao.insertUsers(users)
            
            // Seed accounts
            val accounts = listOf(
                AccountEntity(
                    accountId = "account1",
                    email = "joshitha@example.com",
                    contactNumber = "+1234567890",
                    password = "hashedPassword1",
                    isEmailVerified = true,
                    userId = "user1"
                ),
                AccountEntity(
                    accountId = "account2",
                    email = "ali@example.com",
                    contactNumber = "+1987654321",
                    password = "hashedPassword2",
                    isEmailVerified = true,
                    userId = "user2"
                ),
                AccountEntity(
                    accountId = "account3",
                    email = "sam@example.com",
                    contactNumber = "+1122334455",
                    password = "hashedPassword3",
                    isEmailVerified = true,
                    userId = "user3"
                ),
                AccountEntity(
                    accountId = "account4",
                    email = "emily@example.com",
                    contactNumber = "+1555666777",
                    password = "hashedPassword4",
                    isEmailVerified = true,
                    userId = "user4"
                )
            )
            accountDao.insertAccounts(accounts)
            
            // Seed workspaces
            val workspaces = listOf(
                WorkspaceEntity(
                    workspaceId = "workspace1",
                    name = "Development Team",
                    description = "Main workspace for the development team",
                    createdBy = "user1",
                    leaderId = "user1"
                ),
                WorkspaceEntity(
                    workspaceId = "workspace2",
                    name = "Marketing Team",
                    description = "Marketing team workspace",
                    createdBy = "user2",
                    leaderId = "user2"
                )
            )
            workspaceDao.insertWorkspaces(workspaces)
            
            // Seed workspace memberships
            val workspaceMemberships = listOf(
                WorkspaceUserMembership(
                    userId = "user1",
                    workspaceId = "workspace1",
                    role = "admin"
                ),
                WorkspaceUserMembership(
                    userId = "user2",
                    workspaceId = "workspace1",
                    role = "member"
                ),
                WorkspaceUserMembership(
                    userId = "user3",
                    workspaceId = "workspace1",
                    role = "member"
                ),
                WorkspaceUserMembership(
                    userId = "user2",
                    workspaceId = "workspace2",
                    role = "admin"
                ),
                WorkspaceUserMembership(
                    userId = "user4",
                    workspaceId = "workspace2",
                    role = "member"
                )
            )
            workspaceUserMembershipDao.insertMemberships(workspaceMemberships)
            
            // Seed channels - all with the same name as shown in the UI mockup
            val channels = listOf(
                ChannelEntity(
                    channelId = "channel1",
                    name = "abc-xyz",
                    description = "General discussions",
                    workspaceId = "workspace1",
                    createdBy = "user1",
                    isPrivate = false,
                    unreadCount = 1
                ),
                ChannelEntity(
                    channelId = "channel2",
                    name = "abc-xyz",
                    description = "Development team channel",
                    workspaceId = "workspace1",
                    createdBy = "user1",
                    isPrivate = false,
                    unreadCount = 0
                ),
                ChannelEntity(
                    channelId = "channel3",
                    name = "abc-xyz",
                    description = "Marketing channel",
                    workspaceId = "workspace2",
                    createdBy = "user2",
                    isPrivate = false,
                    unreadCount = 0
                )
            )
            channelDao.insertChannels(channels)
            
            // Seed channel memberships
            val channelMemberships = listOf(
                UserChannelMembership(
                    userId = "user1",
                    channelId = "channel1",
                    joinedAt = System.currentTimeMillis(),
                    role = "admin"
                ),
                UserChannelMembership(
                    userId = "user1",
                    channelId = "channel2",
                    joinedAt = System.currentTimeMillis(),
                    role = "member"
                ),
                UserChannelMembership(
                    userId = "user2",
                    channelId = "channel1",
                    joinedAt = System.currentTimeMillis(),
                    role = "member"
                ),
                UserChannelMembership(
                    userId = "user2",
                    channelId = "channel3",
                    joinedAt = System.currentTimeMillis(),
                    role = "admin"
                ),
                UserChannelMembership(
                    userId = "user4",
                    channelId = "channel3",
                    joinedAt = System.currentTimeMillis(),
                    role = "member"
                )
            )
            userChannelMembershipDao.insertMemberships(channelMemberships)
            
            // Seed epics
            val epics = listOf(
                EpicEntity(
                    epicId = "epic1",
                    name = "Frontend Development",
                    description = "All tasks related to frontend development",
                    workspaceId = "workspace1",
                    createdBy = "user1",
                    priority = 3,
                    status = Status.IN_PROGRESS
                ),
                EpicEntity(
                    epicId = "epic2",
                    name = "Backend Development",
                    description = "All tasks related to backend development",
                    workspaceId = "workspace1",
                    createdBy = "user1",
                    priority = 4,
                    status = Status.IN_PROGRESS
                ),
                EpicEntity(
                    epicId = "epic3",
                    name = "Marketing Campaign",
                    description = "Q3 marketing campaign tasks",
                    workspaceId = "workspace2",
                    createdBy = "user2",
                    priority = 2,
                    status = Status.TO_DO
                )
            )
            epicDao.insertEpics(epics)
            
            // Seed tasks
            val tasks = listOf(
                TaskEntity(
                    taskId = "task1",
                    name = "Your task almost done!",
                    description = "Complete the design review",
                    progress = 20,
                    createdBy = "user1",
                    assignedToUserId = "user1",
                    epicId = "epic1",
                    status = Status.IN_PROGRESS,
                    priority = 2
                ),
                TaskEntity(
                    taskId = "task2",
                    name = "Implement login screen",
                    description = "Create the login screen according to design mockups",
                    progress = 75,
                    createdBy = "user1",
                    assignedToUserId = "user2",
                    epicId = "epic1",
                    status = Status.IN_PROGRESS,
                    priority = 3
                ),
                TaskEntity(
                    taskId = "task3",
                    name = "Build API endpoints",
                    description = "Implement RESTful API endpoints for user management",
                    progress = 30,
                    createdBy = "user1",
                    assignedToUserId = "user3",
                    epicId = "epic2",
                    status = Status.IN_PROGRESS,
                    priority = 4
                ),
                TaskEntity(
                    taskId = "task4",
                    name = "Create social media content",
                    description = "Create content for the Q3 marketing campaign",
                    progress = 0,
                    createdBy = "user2",
                    assignedToUserId = "user4",
                    epicId = "epic3",
                    status = Status.TO_DO,
                    priority = 2
                )
            )
            taskDao.insertTasks(tasks)
            
            // Seed bugs
            val bugs = listOf(
                BugEntity(
                    bugId = "bug1",
                    name = "Login screen crash",
                    description = "App crashes when attempting to login with special characters",
                    createdBy = "user2",
                    taskId = "task2",
                    priority = 5,
                    status = Status.TO_DO
                ),
                BugEntity(
                    bugId = "bug2",
                    name = "API returns 500 error",
                    description = "User creation endpoint returns 500 error with valid data",
                    createdBy = "user1",
                    taskId = "task3",
                    priority = 4,
                    status = Status.IN_PROGRESS
                )
            )
            bugDao.insertBugs(bugs)
            
            // Seed direct messages - show exactly 4 messages from Ali Sarraf as in UI
            val messages = listOf(
                MessageEntity(
                    messageId = UUID.randomUUID().toString(),
                    content = "Hello there!",
                    senderId = "user2",
                    receiverId = "user1",
                    channelId = null,
                    timestamp = System.currentTimeMillis() - 86400000,
                    isRead = true
                ),
                MessageEntity(
                    messageId = UUID.randomUUID().toString(),
                    content = "How's the project going?",
                    senderId = "user2",
                    receiverId = "user1",
                    channelId = null,
                    timestamp = System.currentTimeMillis() - 85400000,
                    isRead = true
                ),
                MessageEntity(
                    messageId = UUID.randomUUID().toString(),
                    content = "Let's meet tomorrow",
                    senderId = "user2",
                    receiverId = "user1",
                    channelId = null,
                    timestamp = System.currentTimeMillis() - 75400000,
                    isRead = false
                ),
                MessageEntity(
                    messageId = UUID.randomUUID().toString(),
                    content = "Have you reviewed the proposal?",
                    senderId = "user2",
                    receiverId = "user1",
                    channelId = null,
                    timestamp = System.currentTimeMillis() - 30000000,
                    isRead = false
                ),
                // Also add unread channel message
                MessageEntity(
                    messageId = UUID.randomUUID().toString(),
                    content = "Team announcement",
                    senderId = "user2",
                    receiverId = null,
                    channelId = "channel1",
                    timestamp = System.currentTimeMillis() - 20000000,
                    isRead = false
                )
            )
            messageDao.insertMessages(messages)
            
            // Seed notifications
            val notifications = listOf(
                NotificationEntity(
                    notificationId = "notif1",
                    content = "Ali Sarraf sent you a message",
                    senderId = "user2",
                    receiverId = "user1",
                    linkTo = "direct/user2",
                    type = "message",
                    status = NotificationStatus.UNREAD
                ),
                NotificationEntity(
                    notificationId = "notif2",
                    content = "New bug reported: Login screen crash",
                    senderId = "user2",
                    receiverId = "user1",
                    linkTo = "task/task2/bug1",
                    type = "bug",
                    status = NotificationStatus.UNREAD
                ),
                NotificationEntity(
                    notificationId = "notif3",
                    content = "You were assigned to task: Implement login screen",
                    senderId = "user1",
                    receiverId = "user2",
                    linkTo = "task/task2",
                    type = "task_assignment",
                    status = NotificationStatus.READ
                )
            )
            notificationDao.insertNotifications(notifications)
            
            // Seed invitations
            val invitations = listOf(
                InvitationEntity(
                    invitationId = "inv1",
                    senderId = "user2",
                    receiverId = "user3",
                    workspaceId = "workspace2",
                    status = InvitationStatus.PENDING
                ),
                InvitationEntity(
                    invitationId = "inv2",
                    senderId = "user1",
                    receiverId = "user4",
                    workspaceId = "workspace1",
                    status = InvitationStatus.ACCEPT
                )
            )
            invitationDao.insertInvitations(invitations)
        }
    }
} 