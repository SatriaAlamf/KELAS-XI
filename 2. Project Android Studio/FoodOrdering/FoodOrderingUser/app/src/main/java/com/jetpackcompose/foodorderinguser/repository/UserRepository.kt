package com.jetpackcompose.foodorderinguser.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jetpackcompose.foodorderinguser.models.User
import com.jetpackcompose.foodorderinguser.models.Address
import com.jetpackcompose.foodorderinguser.utils.Constants
import kotlinx.coroutines.tasks.await

class UserRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection(Constants.COLLECTION_USERS)
    
    suspend fun createUser(user: User): Result<String> {
        return try {
            usersCollection.document(user.id).set(user).await()
            Result.success(user.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUser(userId: String): Result<User?> {
        return try {
            val document = usersCollection.document(userId).get().await()
            val user = document.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.id).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUserProfile(userId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            usersCollection.document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addAddress(userId: String, address: Address): Result<String> {
        return try {
            val user = getUser(userId).getOrNull()
            if (user != null) {
                val updatedAddresses = user.addresses.toMutableList()
                updatedAddresses.add(address)
                val updatedUser = user.copy(addresses = updatedAddresses)
                updateUser(updatedUser).getOrThrow()
                Result.success(address.id)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateAddress(userId: String, address: Address): Result<Unit> {
        return try {
            val user = getUser(userId).getOrNull()
            if (user != null) {
                val updatedAddresses = user.addresses.map { 
                    if (it.id == address.id) address else it 
                }
                val updatedUser = user.copy(addresses = updatedAddresses)
                updateUser(updatedUser).getOrThrow()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteAddress(userId: String, addressId: String): Result<Unit> {
        return try {
            val user = getUser(userId).getOrNull()
            if (user != null) {
                val updatedAddresses = user.addresses.filter { it.id != addressId }
                val updatedUser = user.copy(addresses = updatedAddresses)
                updateUser(updatedUser).getOrThrow()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}