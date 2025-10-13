package com.example.whatsapp.data.repository

import com.example.whatsapp.data.local.MessageDao
import com.example.whatsapp.data.local.ConversationDao
import com.example.whatsapp.data.model.Message
import com.example.whatsapp.data.model.Conversation
import com.example.whatsapp.data.model.MessageStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    
    fun getConversations(): Flow<List<Conversation>> = flow {
        val currentUserId = auth.currentUser?.uid ?: return@flow
        
        // Emit local cache first
        val localConversations = conversationDao.getAllConversations()
        emit(localConversations)
        
        // Then listen to Firestore updates
        try {
            val result = firestore.collection("conversations")
                .whereArrayContains("participants", currentUserId)
                .orderBy("lastUpdated", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val conversations = result.documents.mapNotNull { it.toObject<Conversation>() }
            
            // Update local cache
            conversations.forEach { conversationDao.insertConversation(it) }
            
            emit(conversations)
        } catch (e: Exception) {
            // If Firestore fails, continue with local cache
        }
    }
    
    suspend fun getMessages(conversationId: String, limit: Int = 50, offset: Int = 0): List<Message> {
        return try {
            // Get from local cache first
            val localMessages = messageDao.getMessagesPaginated(conversationId, limit, offset)
            
            if (localMessages.isNotEmpty()) {
                return localMessages
            }
            
            // Get from Firestore
            val result = firestore.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            val messages = result.documents.mapNotNull { it.toObject<Message>() }.reversed()
            
            // Cache locally
            messageDao.insertMessages(messages)
            
            messages
        } catch (e: Exception) {
            messageDao.getMessagesPaginated(conversationId, limit, offset)
        }
    }
    
    suspend fun sendMessage(message: Message): Result<Unit> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
            
            // Add to Firestore
            firestore.collection("conversations")
                .document(message.conversationId)
                .collection("messages")
                .document(message.id)
                .set(message)
                .await()
            
            // Update conversation last message
            updateConversationLastMessage(message)
            
            // Add to local cache
            messageDao.insertMessage(message)
            
            Result.success(Unit)
        } catch (e: Exception) {
            // Mark message as failed in local cache
            messageDao.insertMessage(message.copy(status = MessageStatus.FAILED))
            Result.failure(e)
        }
    }
    
    private suspend fun updateConversationLastMessage(message: Message) {
        try {
            val conversationRef = firestore.collection("conversations").document(message.conversationId)
            val lastMessage = mapOf(
                "id" to message.id,
                "senderId" to message.senderId,
                "senderName" to message.senderName,
                "text" to message.text,
                "type" to message.type.name,
                "timestamp" to message.timestamp,
                "isDeleted" to message.isDeleted
            )
            
            conversationRef.update(
                "lastMessage", lastMessage,
                "lastUpdated", Date()
            ).await()
        } catch (e: Exception) {
            // Handle error silently
        }
    }
    
    suspend fun markMessageAsRead(messageId: String, conversationId: String): Result<Unit> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
            
            // Update in Firestore
            firestore.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .document(messageId)
                .update("readBy.$currentUserId", Date())
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun markMessageAsDelivered(messageId: String, conversationId: String): Result<Unit> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
            
            // Update in Firestore
            firestore.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .document(messageId)
                .update("deliveredTo.$currentUserId", Date())
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createConversation(conversation: Conversation): Result<String> {
        return try {
            // Add to Firestore
            firestore.collection("conversations")
                .document(conversation.id)
                .set(conversation)
                .await()
            
            // Add to local cache
            conversationDao.insertConversation(conversation)
            
            Result.success(conversation.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteMessage(messageId: String, conversationId: String, deleteForEveryone: Boolean = false): Result<Unit> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
            
            if (deleteForEveryone) {
                // Delete for everyone
                firestore.collection("conversations")
                    .document(conversationId)
                    .collection("messages")
                    .document(messageId)
                    .update("isDeleted", true, "text", "This message was deleted")
                    .await()
            } else {
                // Delete for current user only
                firestore.collection("conversations")
                    .document(conversationId)
                    .collection("messages")
                    .document(messageId)
                    .update("deletedFor", listOf(currentUserId))
                    .await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchMessages(query: String): List<Message> {
        return try {
            // Search in local cache first
            // This is a simplified search - in production, you'd want full-text search
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}