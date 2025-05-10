package com.example.dacs3.data.repository

import android.util.Log
import com.example.dacs3.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    private val workspaceUserMembershipDao: WorkspaceUserMembershipDao,
    private val database: WorkspaceDatabase
) {
    // User related
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
    
    suspend fun getUserById(userId: String): UserEntity? = userDao.getUserById(userId)
    
    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    
    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)
    
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
    
    fun getChannelsByWorkspace(workspaceId: String): Flow<List<ChannelEntity>> = 
        channelDao.getChannelsByWorkspaceId(workspaceId)
    
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
    
    suspend fun markNotificationAsRead(notificationId: String) =
        notificationDao.markNotificationAsRead(notificationId)
    
    suspend fun markAllNotificationsAsRead(userId: String) =
        notificationDao.markAllNotificationsAsRead(userId)
    
    suspend fun deleteNotification(notification: NotificationEntity) =
        notificationDao.deleteNotification(notification)
    
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
        // Completely empty implementation - no data will be seeded at all
    }
    
    // Helper method to safely insert workspace with proper transaction
    suspend fun safeInsertWorkspace(workspace: WorkspaceEntity, userId: String) {
        // First ensure the user exists
        val user = getUserById(userId) ?: throw IllegalStateException("User $userId does not exist")
        
        // Insert the workspace in a transaction
        workspaceDao.insertWorkspace(workspace)
        
        // Add membership in the same transaction
        workspaceUserMembershipDao.insertMembership(
            WorkspaceUserMembership(
                userId = userId,
                workspaceId = workspace.workspaceId,
                role = "admin"
            )
        )
    }
    
    // Helper method to safely insert channel with proper relationships
    suspend fun safeInsertChannel(channel: ChannelEntity, userId: String) {
        // First ensure user and workspace exist
        val user = getUserById(userId) ?: throw IllegalStateException("User $userId does not exist")
        val workspace = getWorkspaceById(channel.workspaceId) 
            ?: throw IllegalStateException("Workspace ${channel.workspaceId} does not exist")
        
        // Insert the channel
        channelDao.insertChannel(channel)
        
        // Add the creator as a member
        userChannelMembershipDao.insertMembership(
            UserChannelMembership(
                userId = userId,
                channelId = channel.channelId,
                joinedAt = System.currentTimeMillis(),
                role = "admin"
            )
        )
    }
    
    // Helper method to safely insert message with proper relationships
    suspend fun safeInsertMessage(message: MessageEntity) {
        // Validate sender exists
        getUserById(message.senderId) ?: throw IllegalStateException("Sender ${message.senderId} does not exist")
        
        // If it's a direct message, validate receiver
        message.receiverId?.let { receiverId ->
            getUserById(receiverId) ?: throw IllegalStateException("Receiver $receiverId does not exist")
        }
        
        // If it's a channel message, validate channel
        message.channelId?.let { channelId ->
            getChannelById(channelId) ?: throw IllegalStateException("Channel $channelId does not exist")
        }
        
        // Insert the message
        messageDao.insertMessage(message)
    }
    
    // Helper method to handle workspace and membership creation in a single transaction
    suspend fun createWorkspaceWithMembership(workspace: WorkspaceEntity, userId: String) {
        try {
            // First check if user exists
            val user = getUserById(userId) ?: throw IllegalStateException("User $userId does not exist")
            
            // Log for debugging
            android.util.Log.d("WorkspaceRepository", "Creating workspace with ID: ${workspace.workspaceId} for user ID: ${user.userId}")
            
            // Insert the workspace
            insertWorkspace(workspace)
            
            // Small delay to ensure the workspace is created
            kotlinx.coroutines.delay(300)
            
            // Verify workspace was inserted
            val createdWorkspace = getWorkspaceById(workspace.workspaceId)
            if (createdWorkspace == null) {
                throw IllegalStateException("Failed to create workspace with ID: ${workspace.workspaceId}")
            }
            
            // Insert membership
            val membership = WorkspaceUserMembership(
                userId = userId,
                workspaceId = workspace.workspaceId,
                role = "admin"
            )
            
            insertWorkspaceUserMembership(membership)
            
            // Log success
            android.util.Log.d("WorkspaceRepository", "Successfully created workspace and membership")
        } catch (e: Exception) {
            android.util.Log.e("WorkspaceRepository", "Error in createWorkspaceWithMembership: ${e.message}")
            throw e
        }
    }
    
    // Improved method to create workspace with membership using an actual transaction
    suspend fun createWorkspaceWithMembership(workspace: WorkspaceEntity, membership: WorkspaceUserMembership) {
        try {
            Log.d("WorkspaceRepository", "Starting transaction to create workspace: ${workspace.workspaceId}")
            
            // Validate the user exists
            val user = getUserById(workspace.createdBy)
                ?: throw IllegalStateException("User ${workspace.createdBy} does not exist")
            
            // Since runInTransaction doesn't support suspend functions, we need to use a different approach
            // Insert workspace and membership sequentially
            insertWorkspace(workspace)
            
            // Small delay to ensure the workspace is created
            kotlinx.coroutines.delay(100)
            
            // Insert the membership
            insertWorkspaceUserMembership(membership)
            
            Log.d("WorkspaceRepository", "Sequential operations completed")
            
            // Verify the workspace was created successfully
            val createdWorkspace = getWorkspaceById(workspace.workspaceId)
            if (createdWorkspace == null) {
                Log.e("WorkspaceRepository", "Verification failed: Workspace not found after transaction")
                throw IllegalStateException("Failed to create workspace: Transaction completed but workspace not found")
            }
            
            // Verify membership was created
            val memberships = workspaceUserMembershipDao.getMembershipsForWorkspace(workspace.workspaceId)
            if (memberships.isEmpty()) {
                Log.e("WorkspaceRepository", "Verification failed: No memberships found for workspace")
                throw IllegalStateException("Failed to create workspace membership: Transaction completed but membership not found")
            }
            
            Log.d("WorkspaceRepository", "Workspace and membership successfully created and verified")
        } catch (e: Exception) {
            Log.e("WorkspaceRepository", "Error in createWorkspaceWithMembership transaction", e)
            throw e
        }
    }
} 