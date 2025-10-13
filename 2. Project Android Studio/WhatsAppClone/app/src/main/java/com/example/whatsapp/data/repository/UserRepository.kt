package com.example.whatsapp.data.repository

import com.example.whatsapp.data.local.UserDao
import com.example.whatsapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    
    fun getCurrentUser(): Flow<User?> = flow {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            // Try to get from local cache first
            val localUser = userDao.getUserById(currentUserId)
            emit(localUser)
            
            // Then get from Firestore
            try {
                val firestoreUser = firestore.collection("users")
                    .document(currentUserId)
                    .get()
                    .await()
                    .toObject<User>()
                
                if (firestoreUser != null) {
                    userDao.insertUser(firestoreUser)
                    emit(firestoreUser)
                }
            } catch (e: Exception) {
                // Handle error - emit local cache or null
                if (localUser == null) {
                    emit(null)
                }
            }
        } else {
            emit(null)
        }
    }
    
    suspend fun getUserById(userId: String): User? {
        return try {
            // Try local cache first
            val localUser = userDao.getUserById(userId)
            if (localUser != null) {
                return localUser
            }
            
            // Get from Firestore
            val firestoreUser = firestore.collection("users")
                .document(userId)
                .get()
                .await()
                .toObject<User>()
            
            if (firestoreUser != null) {
                userDao.insertUser(firestoreUser)
            }
            
            firestoreUser
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            // Update in Firestore
            firestore.collection("users")
                .document(user.uid)
                .set(user)
                .await()
            
            // Update in local cache
            userDao.updateUser(user)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createUser(user: User): Result<Unit> {
        return try {
            // Create in Firestore
            firestore.collection("users")
                .document(user.uid)
                .set(user)
                .await()
            
            // Save to local cache
            userDao.insertUser(user)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUserPresence(userId: String, isOnline: Boolean) {
        try {
            firestore.collection("users")
                .document(userId)
                .update("isOnline", isOnline, "lastSeen", System.currentTimeMillis())
                .await()
        } catch (e: Exception) {
            // Handle error silently for presence updates
        }
    }
    
    suspend fun updateFCMToken(token: String): Result<Unit> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
            
            firestore.collection("users")
                .document(currentUserId)
                .update("fcmTokens", listOf(token))
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchUsers(query: String): List<User> {
        return try {
            val result = firestore.collection("users")
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(20)
                .get()
                .await()
            
            result.documents.mapNotNull { it.toObject<User>() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}