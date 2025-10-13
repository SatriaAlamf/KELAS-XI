package com.jetpackcompose.foodorderinguser.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.jetpackcompose.foodorderinguser.models.User
import com.jetpackcompose.foodorderinguser.repository.UserRepository
import kotlinx.coroutines.tasks.await

class AuthManager {
    
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()
    
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
    
    fun isUserLoggedIn(): Boolean {
        return getCurrentUser() != null
    }
    
    suspend fun loginWithEmail(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                val user = userRepository.getUser(firebaseUser.uid).getOrNull()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("User data not found"))
                }
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun registerWithEmail(
        name: String,
        email: String,
        password: String,
        phone: String
    ): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                // Update Firebase user profile
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()
                
                // Create user document in Firestore
                val user = User(
                    id = firebaseUser.uid,
                    name = name,
                    email = email,
                    phone = phone,
                    createdAt = System.currentTimeMillis()
                )
                
                userRepository.createUser(user).getOrThrow()
                Result.success(user)
            } else {
                Result.failure(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            val user = getCurrentUser()
            if (user != null) {
                user.updatePassword(newPassword).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateEmail(newEmail: String): Result<Unit> {
        return try {
            val user = getCurrentUser()
            if (user != null) {
                user.updateEmail(newEmail).await()
                // Also update in Firestore
                userRepository.updateUserProfile(
                    user.uid, 
                    mapOf("email" to newEmail)
                ).getOrThrow()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateProfile(name: String): Result<Unit> {
        return try {
            val user = getCurrentUser()
            if (user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                user.updateProfile(profileUpdates).await()
                
                // Also update in Firestore
                userRepository.updateUserProfile(
                    user.uid, 
                    mapOf("name" to name)
                ).getOrThrow()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = getCurrentUser()
            if (user != null) {
                user.delete().await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun logout() {
        firebaseAuth.signOut()
    }
    
    suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            val user = getCurrentUser()
            if (user != null) {
                user.sendEmailVerification().await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun isEmailVerified(): Boolean {
        return getCurrentUser()?.isEmailVerified ?: false
    }
}