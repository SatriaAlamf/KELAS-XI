package com.jetpackcompose.foodorderingadmin.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jetpackcompose.foodorderingadmin.models.Category
import com.jetpackcompose.foodorderingadmin.utils.Constants
import kotlinx.coroutines.tasks.await

class CategoryRepository {
    
    private val db = FirebaseFirestore.getInstance()
    private val categoriesCollection = db.collection(Constants.COLLECTION_CATEGORIES)
    
    suspend fun getAllCategories(): Result<List<Category>> {
        return try {
            val snapshot = categoriesCollection
                .orderBy("sortOrder", Query.Direction.ASCENDING)
                .get()
                .await()
            
            val categories = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Category::class.java)?.copy(id = doc.id)
            }
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCategoryById(categoryId: String): Result<Category> {
        return try {
            val document = categoriesCollection.document(categoryId).get().await()
            val category = document.toObject(Category::class.java)?.copy(id = document.id)
            
            if (category != null) {
                Result.success(category)
            } else {
                Result.failure(Exception("Category not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addCategory(category: Category): Result<String> {
        return try {
            val docRef = categoriesCollection.add(category).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateCategory(categoryId: String, category: Category): Result<Unit> {
        return try {
            categoriesCollection.document(categoryId).set(category).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            categoriesCollection.document(categoryId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun toggleCategoryStatus(categoryId: String, isActive: Boolean): Result<Unit> {
        return try {
            categoriesCollection.document(categoryId)
                .update("isActive", isActive)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
