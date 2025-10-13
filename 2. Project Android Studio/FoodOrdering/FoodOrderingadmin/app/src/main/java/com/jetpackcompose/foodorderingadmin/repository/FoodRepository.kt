package com.jetpackcompose.foodorderingadmin.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jetpackcompose.foodorderingadmin.models.Food
import com.jetpackcompose.foodorderingadmin.utils.Constants
import kotlinx.coroutines.tasks.await

class FoodRepository {
    
    private val db = FirebaseFirestore.getInstance()
    private val foodsCollection = db.collection(Constants.COLLECTION_FOODS)
    
    suspend fun getAllFoods(): Result<List<Food>> {
        return try {
            val snapshot = foodsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val foods = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Food::class.java)?.copy(id = doc.id)
            }
            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getFoodsByCategory(categoryId: String): Result<List<Food>> {
        return try {
            val snapshot = foodsCollection
                .whereEqualTo("categoryId", categoryId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val foods = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Food::class.java)?.copy(id = doc.id)
            }
            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getFoodById(foodId: String): Result<Food> {
        return try {
            val document = foodsCollection.document(foodId).get().await()
            val food = document.toObject(Food::class.java)?.copy(id = document.id)
            
            if (food != null) {
                Result.success(food)
            } else {
                Result.failure(Exception("Food not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addFood(food: Food): Result<String> {
        return try {
            val docRef = foodsCollection.add(food).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateFood(foodId: String, food: Food): Result<Unit> {
        return try {
            val updatedFood = food.copy(updatedAt = System.currentTimeMillis())
            foodsCollection.document(foodId).set(updatedFood).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteFood(foodId: String): Result<Unit> {
        return try {
            foodsCollection.document(foodId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun toggleFoodAvailability(foodId: String, isAvailable: Boolean): Result<Unit> {
        return try {
            foodsCollection.document(foodId)
                .update(mapOf(
                    "isAvailable" to isAvailable,
                    "updatedAt" to System.currentTimeMillis()
                ))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchFoods(query: String): Result<List<Food>> {
        return try {
            val snapshot = foodsCollection.get().await()
            
            val foods = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Food::class.java)?.copy(id = doc.id)
            }.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.categoryName.contains(query, ignoreCase = true)
            }
            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
