package com.example.whatsapp.data.repository

import com.example.whatsapp.data.local.StatusDao
import com.example.whatsapp.data.model.Status
import com.example.whatsapp.data.model.StatusViewer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatusRepository @Inject constructor(
    private val statusDao: StatusDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    
    fun getAllStatus(): Flow<List<Status>> = flow {
        val currentTime = System.currentTimeMillis()
        
        // Clean up expired status from local cache
        statusDao.deleteExpiredStatus(currentTime)
        
        // Emit local cache first
        val localStatus = statusDao.getAllStatus()
        emit(localStatus)
        
        // Then get from Firestore
        try {
            val result = firestore.collection("status")
                .whereGreaterThan("expiresAt", Date(currentTime))
                .orderBy("expiresAt")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val statusList = result.documents.mapNotNull { it.toObject<Status>() }
            
            // Update local cache
            statusList.forEach { statusDao.insertStatus(it) }
            
            emit(statusList)
        } catch (e: Exception) {
            // Continue with local cache on error
        }
    }
    
    suspend fun getMyStatus(): List<Status> {
        val currentUserId = auth.currentUser?.uid ?: return emptyList()
        
        return try {
            val currentTime = System.currentTimeMillis()
            
            val result = firestore.collection("status")
                .whereEqualTo("ownerId", currentUserId)
                .whereGreaterThan("expiresAt", Date(currentTime))
                .orderBy("expiresAt")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            result.documents.mapNotNull { it.toObject<Status>() }
        } catch (e: Exception) {
            statusDao.getStatusByOwnerId(currentUserId)
        }
    }
    
    suspend fun uploadStatus(status: Status): Result<Unit> {
        return try {
            // Add to Firestore
            firestore.collection("status")
                .document(status.id)
                .set(status)
                .await()
            
            // Add to local cache
            statusDao.insertStatus(status)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun viewStatus(statusId: String): Result<Unit> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
            val currentUser = auth.currentUser
            
            val viewer = StatusViewer(
                userId = currentUserId,
                userName = currentUser?.displayName ?: "Unknown",
                viewedAt = Date()
            )
            
            // Update in Firestore
            firestore.collection("status")
                .document(statusId)
                .update("viewers", com.google.firebase.firestore.FieldValue.arrayUnion(viewer))
                .await()
            
            // Update local cache
            val localStatus = statusDao.getStatusById(statusId)
            if (localStatus != null) {
                val updatedViewers = localStatus.viewers.toMutableList()
                updatedViewers.removeAll { it.userId == currentUserId }
                updatedViewers.add(viewer)
                
                statusDao.updateStatus(
                    localStatus.copy(
                        viewers = updatedViewers,
                        isViewed = true,
                        viewedAt = Date()
                    )
                )
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteStatus(statusId: String): Result<Unit> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
            
            // Check if user owns the status
            val status = firestore.collection("status")
                .document(statusId)
                .get()
                .await()
                .toObject<Status>()
            
            if (status?.ownerId != currentUserId) {
                return Result.failure(Exception("Cannot delete status that doesn't belong to you"))
            }
            
            // Delete from Firestore
            firestore.collection("status")
                .document(statusId)
                .delete()
                .await()
            
            // Delete from local cache
            statusDao.deleteStatusById(statusId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun cleanupExpiredStatus() {
        try {
            val currentTime = System.currentTimeMillis()
            
            // Clean up from Firestore (this should ideally be done via Cloud Functions)
            val expiredStatus = firestore.collection("status")
                .whereLessThan("expiresAt", Date(currentTime))
                .get()
                .await()
            
            val batch = firestore.batch()
            expiredStatus.documents.forEach { document ->
                batch.delete(document.reference)
            }
            batch.commit().await()
            
            // Clean up local cache
            statusDao.deleteExpiredStatus(currentTime)
        } catch (e: Exception) {
            // Handle error silently
        }
    }
    
    fun createExpiryDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, 24)
        return calendar.time
    }
}