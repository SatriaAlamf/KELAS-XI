package com.example.whatsapp.data.local

import androidx.room.*
import com.example.whatsapp.data.model.*

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUserById(uid: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM users WHERE uid = :uid")
    suspend fun deleteUserById(uid: String)
}

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations ORDER BY lastUpdated DESC")
    suspend fun getAllConversations(): List<Conversation>

    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getConversationById(id: String): Conversation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation)

    @Update
    suspend fun updateConversation(conversation: Conversation)

    @Delete
    suspend fun deleteConversation(conversation: Conversation)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversationById(id: String)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    suspend fun getMessagesByConversationId(conversationId: String): List<Message>

    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getMessageById(id: String): Message?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)

    @Update
    suspend fun updateMessage(message: Message)

    @Delete
    suspend fun deleteMessage(message: Message)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessageById(id: String)

    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesByConversationId(conversationId: String)

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getMessagesPaginated(conversationId: String, limit: Int, offset: Int): List<Message>
}

@Dao
interface StatusDao {
    @Query("SELECT * FROM status ORDER BY createdAt DESC")
    suspend fun getAllStatus(): List<Status>

    @Query("SELECT * FROM status WHERE ownerId = :ownerId ORDER BY createdAt DESC")
    suspend fun getStatusByOwnerId(ownerId: String): List<Status>

    @Query("SELECT * FROM status WHERE id = :id")
    suspend fun getStatusById(id: String): Status?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatus(status: Status)

    @Update
    suspend fun updateStatus(status: Status)

    @Delete
    suspend fun deleteStatus(status: Status)

    @Query("DELETE FROM status WHERE id = :id")
    suspend fun deleteStatusById(id: String)

    @Query("DELETE FROM status WHERE expiresAt < :currentTime")
    suspend fun deleteExpiredStatus(currentTime: Long)
}

@Dao
interface CallDao {
    @Query("SELECT * FROM calls ORDER BY startTime DESC")
    suspend fun getAllCalls(): List<Call>

    @Query("SELECT * FROM calls WHERE id = :id")
    suspend fun getCallById(id: String): Call?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCall(call: Call)

    @Update
    suspend fun updateCall(call: Call)

    @Delete
    suspend fun deleteCall(call: Call)

    @Query("DELETE FROM calls WHERE id = :id")
    suspend fun deleteCallById(id: String)
}

@Dao
interface CommunityDao {
    @Query("SELECT * FROM communities ORDER BY updatedAt DESC")
    suspend fun getAllCommunities(): List<Community>

    @Query("SELECT * FROM communities WHERE id = :id")
    suspend fun getCommunityById(id: String): Community?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunity(community: Community)

    @Update
    suspend fun updateCommunity(community: Community)

    @Delete
    suspend fun deleteCommunity(community: Community)

    @Query("DELETE FROM communities WHERE id = :id")
    suspend fun deleteCommunityById(id: String)
}