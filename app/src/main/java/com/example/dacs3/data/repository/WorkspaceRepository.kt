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
    
    fun getUserWorkspaces(userId: String): Flow<List<WorkspaceEntity>> = workspaceDao.getWorkspacesByMember(userId)
    
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
    
    fun getTasksByEpic(epicId: String): Flow<List<TaskEntity>> = taskDao.getTasksByEpic(epicId)
    
    suspend fun insertTask(task: TaskEntity) = taskDao.insertTask(task)
    
    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)
    
    suspend fun getTaskById(taskId: String): TaskEntity? = taskDao.getTaskById(taskId)
    
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
        // No longer pre-populating data as per requirement
        // Data will be created through user interactions
    }
} 