package com.jetpackcompose.foodorderingadmin.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.jetpackcompose.foodorderingadmin.models.AdminUser
import com.jetpackcompose.foodorderingadmin.utils.Constants
import kotlinx.coroutines.tasks.await

class AuthManager {
    
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    
    fun isLoggedIn(): Boolean = auth.currentUser != null
    
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                // Verify if user is admin
                val isAdmin = checkIfAdmin(user.uid)
                if (isAdmin) {
                    Result.success(user)
                } else {
                    auth.signOut()
                    Result.failure(Exception("Access denied. Admin privileges required."))
                }
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun register(email: String, password: String, name: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                // Create admin user document
                val adminUser = AdminUser(
                    id = user.uid,
                    name = name,
                    email = email
                )
                
                db.collection(Constants.COLLECTION_ADMIN_USERS)
                    .document(user.uid)
                    .set(adminUser)
                    .await()
                
                Result.success(user)
            } else {
                Result.failure(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun logout() {
        auth.signOut()
    }
    
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun checkIfAdmin(userId: String): Boolean {
        return try {
            val document = db.collection(Constants.COLLECTION_ADMIN_USERS)
                .document(userId)
                .get()
                .await()
            
            document.exists()
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun getAdminUser(userId: String): Result<AdminUser> {
        return try {
            val document = db.collection(Constants.COLLECTION_ADMIN_USERS)
                .document(userId)
                .get()
                .await()
            
            val adminUser = document.toObject(AdminUser::class.java)
            
            if (adminUser != null) {
                Result.success(adminUser.copy(id = document.id))
            } else {
                Result.failure(Exception("Admin user not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
