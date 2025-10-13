package com.example.whatsapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsapp.data.model.User
import com.example.whatsapp.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId

    fun signInWithEmailPassword(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Authentication failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createUserWithEmailPassword(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    // Create user profile
                    val user = User(
                        uid = firebaseUser.uid,
                        name = name,
                        email = email,
                        phoneNumber = firebaseUser.phoneNumber ?: "",
                        createdAt = Date(),
                        updatedAt = Date()
                    )
                    
                    val createResult = userRepository.createUser(user)
                    if (createResult.isSuccess) {
                        _authState.value = AuthState.Success
                    } else {
                        _authState.value = AuthState.Error("Failed to create user profile")
                    }
                } else {
                    _authState.value = AuthState.Error("Failed to create account")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun signInWithGoogleCredential(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val firebaseUser = result.user
                
                if (firebaseUser != null) {
                    // Check if user exists, if not create profile
                    val existingUser = userRepository.getUserById(firebaseUser.uid)
                    if (existingUser == null) {
                        val user = User(
                            uid = firebaseUser.uid,
                            name = firebaseUser.displayName ?: "",
                            email = firebaseUser.email ?: "",
                            phoneNumber = firebaseUser.phoneNumber ?: "",
                            avatarUrl = firebaseUser.photoUrl?.toString() ?: "",
                            createdAt = Date(),
                            updatedAt = Date()
                        )
                        userRepository.createUser(user)
                    }
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Google sign-in failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google sign-in error")
            }
        }
    }

    fun signInWithFacebookToken(token: AccessToken) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val credential = FacebookAuthProvider.getCredential(token.token)
                val result = auth.signInWithCredential(credential).await()
                val firebaseUser = result.user
                
                if (firebaseUser != null) {
                    // Check if user exists, if not create profile
                    val existingUser = userRepository.getUserById(firebaseUser.uid)
                    if (existingUser == null) {
                        val user = User(
                            uid = firebaseUser.uid,
                            name = firebaseUser.displayName ?: "",
                            email = firebaseUser.email ?: "",
                            phoneNumber = firebaseUser.phoneNumber ?: "",
                            avatarUrl = firebaseUser.photoUrl?.toString() ?: "",
                            createdAt = Date(),
                            updatedAt = Date()
                        )
                        userRepository.createUser(user)
                    }
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Facebook sign-in failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Facebook sign-in error")
            }
        }
    }

    fun verifyPhoneCredential(credential: PhoneAuthCredential, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithCredential(credential).await()
                val firebaseUser = result.user
                
                if (firebaseUser != null) {
                    // Check if user exists, if not create profile
                    val existingUser = userRepository.getUserById(firebaseUser.uid)
                    if (existingUser == null) {
                        val user = User(
                            uid = firebaseUser.uid,
                            name = name,
                            email = "",
                            phoneNumber = firebaseUser.phoneNumber ?: "",
                            createdAt = Date(),
                            updatedAt = Date()
                        )
                        userRepository.createUser(user)
                    }
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Phone verification failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Phone verification error")
            }
        }
    }

    fun setVerificationId(id: String) {
        _verificationId.value = id
    }

    fun resetAuthState() {
        _authState.value = AuthState.Initial
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Initial
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

// Extension function for await
suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T {
    return kotlinx.coroutines.tasks.await(this)
}