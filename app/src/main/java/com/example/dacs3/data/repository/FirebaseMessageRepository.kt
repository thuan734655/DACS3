package com.example.dacs3.data.repository

import android.util.Log
import com.example.dacs3.data.model.Conversation
import com.example.dacs3.data.model.RealtimeMessage
import com.example.dacs3.data.model.User
import com.example.dacs3.data.session.SessionManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMessageRepository @Inject constructor(
    private val sessionManager: SessionManager
) {
    private val TAG = "FirebaseMessageRepo"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val messagesRef: DatabaseReference = database.getReference("messages")
    private val conversationsRef: DatabaseReference = database.getReference("conversations")
    private val workspaceMembersRef: DatabaseReference = database.getReference("workspace_members")

    /**
     * Send a message to a workspace chat
     */
    suspend fun sendMessage(workspaceId: String, content: String, senderName: String): Boolean {
        return try {
            val userId = sessionManager.getUserId()
            if (userId.isNullOrEmpty()) {
                Log.e(TAG, "Cannot send message: No user ID available")
                return false
            }

            val messageId = UUID.randomUUID().toString()
            val timestamp = System.currentTimeMillis()
            
            val message = RealtimeMessage(
                id = messageId,
                workspaceId = workspaceId,
                senderId = userId,
                senderName = senderName,
                content = content,
                timestamp = timestamp,
                read = false
            )
            
            // Save the message
            messagesRef.child(workspaceId).child(messageId).setValue(message).await()
            
            // Update the conversation metadata
            val conversationUpdates = mapOf(
                "lastMessage" to content,
                "lastMessageTime" to timestamp
            )
            conversationsRef.child(workspaceId).updateChildren(conversationUpdates).await()
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message: ${e.message}", e)
            false
        }
    }

    /**
     * Get messages for a specific workspace as a Flow
     */
    fun getWorkspaceMessages(workspaceId: String): Flow<List<RealtimeMessage>> = callbackFlow {
        val messagesListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<RealtimeMessage>()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(RealtimeMessage::class.java)
                    message?.let { messages.add(it) }
                }
                
                // Sort messages by timestamp (newest first)
                messages.sortByDescending { it.timestamp }
                
                trySend(messages).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error loading workspace messages: ${error.message}", error.toException())
                close(error.toException())
            }
        }
        
        messagesRef.child(workspaceId).addValueEventListener(messagesListener)
        
        awaitClose {
            messagesRef.child(workspaceId).removeEventListener(messagesListener)
        }
    }

    /**
     * Get all workspace conversations for the current user
     */
    fun getUserWorkspaceConversations(): Flow<List<Conversation>> = callbackFlow {
        val userId = sessionManager.getUserId()
        if (userId.isNullOrEmpty()) {
            close(IllegalStateException("No user ID available"))
            return@callbackFlow
        }
        
        val conversationsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val conversations = mutableListOf<Conversation>()
                for (convoSnapshot in snapshot.children) {
                    val conversation = convoSnapshot.getValue(Conversation::class.java)
                    conversation?.let { 
                        // Only add conversations where the user is a participant
                        if (it.participants.containsKey(userId)) {
                            conversations.add(it) 
                        }
                    }
                }
                
                // Sort conversations by last message time (newest first)
                conversations.sortByDescending { it.lastMessageTime }
                
                trySend(conversations).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error loading conversations: ${error.message}", error.toException())
                close(error.toException())
            }
        }
        
        conversationsRef.addValueEventListener(conversationsListener)
        
        awaitClose {
            conversationsRef.removeEventListener(conversationsListener)
        }
    }

    /**
     * Create a new conversation for a workspace
     */
    suspend fun createWorkspaceConversation(workspaceId: String, workspaceName: String, members: List<User>): Boolean {
        return try {
            val participants = members.associate { it._id to true }
            
            val conversation = Conversation(
                id = workspaceId,
                name = workspaceName,
                lastMessage = "Conversation started",
                lastMessageTime = System.currentTimeMillis(),
                participants = participants
            )
            
            conversationsRef.child(workspaceId).setValue(conversation).await()
            
            // Create a mapping of workspace to members for quick lookup
            members.forEach { member ->
                workspaceMembersRef.child(workspaceId).child(member._id).setValue(true).await()
            }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating workspace conversation: ${e.message}", e)
            false
        }
    }

    /**
     * Mark all messages in a workspace as read for the current user
     */
    suspend fun markWorkspaceMessagesAsRead(workspaceId: String): Boolean {
        return try {
            val userId = sessionManager.getUserId() ?: return false
            
            // Get all unread messages for this workspace
            val messagesSnapshot = messagesRef.child(workspaceId)
                .orderByChild("read")
                .equalTo(false)
                .get()
                .await()
            
            // Mark each unread message as read
            for (messageSnapshot in messagesSnapshot.children) {
                val message = messageSnapshot.getValue(RealtimeMessage::class.java)
                if (message != null && message.senderId != userId) {
                    messageSnapshot.ref.child("read").setValue(true)
                }
            }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error marking messages as read: ${e.message}", e)
            false
        }
    }
}
