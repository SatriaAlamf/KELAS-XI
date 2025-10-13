package com.jetpackcompose.foodorderinguser.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.jetpackcompose.foodorderinguser.models.Cart
import com.jetpackcompose.foodorderinguser.models.CartItem
import com.jetpackcompose.foodorderinguser.utils.Constants
import kotlinx.coroutines.tasks.await

class CartRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val cartCollection = firestore.collection(Constants.COLLECTION_CART)
    
    suspend fun getCart(userId: String): Result<Cart?> {
        return try {
            val document = cartCollection.document(userId).get().await()
            val cart = document.toObject(Cart::class.java)
            Result.success(cart)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addItemToCart(userId: String, cartItem: CartItem): Result<Unit> {
        return try {
            val currentCart = getCart(userId).getOrNull() ?: Cart(userId = userId)
            val existingItems = currentCart.items.toMutableList()
            
            // Check if item already exists in cart
            val existingItemIndex = existingItems.indexOfFirst { 
                it.foodId == cartItem.foodId && it.selectedAddons == cartItem.selectedAddons 
            }
            
            if (existingItemIndex != -1) {
                // Update quantity if item exists
                val existingItem = existingItems[existingItemIndex]
                existingItems[existingItemIndex] = existingItem.copy(
                    quantity = existingItem.quantity + cartItem.quantity
                )
            } else {
                // Add new item
                existingItems.add(cartItem)
            }
            
            val updatedCart = currentCart.copy(
                items = existingItems,
                updatedAt = System.currentTimeMillis()
            ).calculateTotals()
            
            cartCollection.document(userId).set(updatedCart).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateCartItem(userId: String, cartItem: CartItem): Result<Unit> {
        return try {
            val currentCart = getCart(userId).getOrNull() ?: return Result.failure(Exception("Cart not found"))
            val updatedItems = currentCart.items.map { 
                if (it.id == cartItem.id) cartItem else it 
            }
            
            val updatedCart = currentCart.copy(
                items = updatedItems,
                updatedAt = System.currentTimeMillis()
            ).calculateTotals()
            
            cartCollection.document(userId).set(updatedCart).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun removeItemFromCart(userId: String, cartItemId: String): Result<Unit> {
        return try {
            val currentCart = getCart(userId).getOrNull() ?: return Result.failure(Exception("Cart not found"))
            val updatedItems = currentCart.items.filter { it.id != cartItemId }
            
            val updatedCart = currentCart.copy(
                items = updatedItems,
                updatedAt = System.currentTimeMillis()
            ).calculateTotals()
            
            cartCollection.document(userId).set(updatedCart).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateItemQuantity(userId: String, cartItemId: String, quantity: Int): Result<Unit> {
        return try {
            val currentCart = getCart(userId).getOrNull() ?: return Result.failure(Exception("Cart not found"))
            
            if (quantity <= 0) {
                return removeItemFromCart(userId, cartItemId)
            }
            
            val updatedItems = currentCart.items.map { item ->
                if (item.id == cartItemId) {
                    item.copy(quantity = quantity)
                } else {
                    item
                }
            }
            
            val updatedCart = currentCart.copy(
                items = updatedItems,
                updatedAt = System.currentTimeMillis()
            ).calculateTotals()
            
            cartCollection.document(userId).set(updatedCart).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun clearCart(userId: String): Result<Unit> {
        return try {
            val emptyCart = Cart(
                userId = userId,
                items = emptyList(),
                updatedAt = System.currentTimeMillis()
            )
            
            cartCollection.document(userId).set(emptyCart).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun applyDiscount(userId: String, discountAmount: Double): Result<Unit> {
        return try {
            val currentCart = getCart(userId).getOrNull() ?: return Result.failure(Exception("Cart not found"))
            
            val updatedCart = currentCart.copy(
                discount = discountAmount,
                updatedAt = System.currentTimeMillis()
            ).calculateTotals()
            
            cartCollection.document(userId).set(updatedCart).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}