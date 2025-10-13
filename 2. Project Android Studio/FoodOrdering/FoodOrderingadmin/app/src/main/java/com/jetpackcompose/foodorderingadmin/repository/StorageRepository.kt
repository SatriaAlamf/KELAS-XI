package com.jetpackcompose.foodorderingadmin.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jetpackcompose.foodorderingadmin.utils.Constants
import kotlinx.coroutines.tasks.await
import android.net.Uri

class StorageRepository {
    
    private val storage = FirebaseStorage.getInstance()
    
    suspend fun uploadFoodImage(imageUri: Uri, fileName: String): Result<String> {
        return try {
            val storageRef = storage.reference
                .child(Constants.STORAGE_FOOD_IMAGES)
                .child("${System.currentTimeMillis()}_$fileName")
            
            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun uploadCategoryImage(imageUri: Uri, fileName: String): Result<String> {
        return try {
            val storageRef = storage.reference
                .child(Constants.STORAGE_CATEGORY_IMAGES)
                .child("${System.currentTimeMillis()}_$fileName")
            
            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
            storageRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
