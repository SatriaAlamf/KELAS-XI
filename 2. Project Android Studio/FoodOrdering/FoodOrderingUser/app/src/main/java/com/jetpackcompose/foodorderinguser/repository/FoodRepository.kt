package com.jetpackcompose.foodorderinguser.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jetpackcompose.foodorderinguser.models.Food
import com.jetpackcompose.foodorderinguser.models.Category
import com.jetpackcompose.foodorderinguser.utils.Constants
import kotlinx.coroutines.tasks.await

class FoodRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val categoriesCollection = firestore.collection(Constants.COLLECTION_CATEGORIES)
    private val foodsCollection = firestore.collection(Constants.COLLECTION_FOODS)
    
    suspend fun getAllCategories(): Result<List<Category>> {
        return try {
            val querySnapshot = categoriesCollection
                .whereEqualTo("isActive", true)
                .orderBy("sortOrder")
                .get()
                .await()
            
            val categories = querySnapshot.toObjects(Category::class.java)
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getFoodsByCategory(categoryId: String): Result<List<Food>> {
        return try {
            val querySnapshot = foodsCollection
                .whereEqualTo("categoryId", categoryId)
                .whereEqualTo("isAvailable", true)
                .orderBy("name")
                .get()
                .await()
            
            val foods = querySnapshot.toObjects(Food::class.java)
            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAllFoods(): Result<List<Food>> {
        return try {
            val querySnapshot = foodsCollection
                .whereEqualTo("isAvailable", true)
                .orderBy("name")
                .get()
                .await()
            
            val foods = querySnapshot.toObjects(Food::class.java)
            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getFoodById(foodId: String): Result<Food?> {
        return try {
            val document = foodsCollection.document(foodId).get().await()
            val food = document.toObject(Food::class.java)
            Result.success(food)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchFoods(query: String): Result<List<Food>> {
        return try {
            val querySnapshot = foodsCollection
                .whereEqualTo("isAvailable", true)
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()
            
            val foods = querySnapshot.toObjects(Food::class.java)
            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getFoodsByTag(tag: String): Result<List<Food>> {
        return try {
            val querySnapshot = foodsCollection
                .whereArrayContains("tags", tag)
                .whereEqualTo("isAvailable", true)
                .orderBy("name")
                .get()
                .await()
            
            val foods = querySnapshot.toObjects(Food::class.java)
            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPopularFoods(): Result<List<Food>> {
        return try {
            val querySnapshot = foodsCollection
                .whereEqualTo("isAvailable", true)
                .whereGreaterThan("rating", 4.0)
                .orderBy("rating", Query.Direction.DESCENDING)
                .orderBy("reviewCount", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .await()
            
            val foods = querySnapshot.toObjects(Food::class.java)
            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getDiscountedFoods(): Result<List<Food>> {
        return try {
            val querySnapshot = foodsCollection
                .whereEqualTo("isAvailable", true)
                .whereGreaterThan("discount", 0)
                .orderBy("discount", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .await()
            
            val foods = querySnapshot.toObjects(Food::class.java)
            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}