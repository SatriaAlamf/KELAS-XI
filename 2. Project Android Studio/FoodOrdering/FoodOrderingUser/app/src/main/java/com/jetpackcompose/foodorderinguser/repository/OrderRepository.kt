package com.jetpackcompose.foodorderinguser.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jetpackcompose.foodorderinguser.models.Order
import com.jetpackcompose.foodorderinguser.models.OrderStatus
import com.jetpackcompose.foodorderinguser.utils.Constants
import kotlinx.coroutines.tasks.await

class OrderRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val ordersCollection = firestore.collection(Constants.COLLECTION_ORDERS)
    
    suspend fun createOrder(order: Order): Result<String> {
        return try {
            val orderRef = ordersCollection.document()
            val orderWithId = order.copy(id = orderRef.id)
            orderRef.set(orderWithId).await()
            Result.success(orderRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getOrder(orderId: String): Result<Order?> {
        return try {
            val document = ordersCollection.document(orderId).get().await()
            val order = document.toObject(Order::class.java)
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserOrders(userId: String): Result<List<Order>> {
        return try {
            val querySnapshot = ordersCollection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val orders = querySnapshot.toObjects(Order::class.java)
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserOrdersByStatus(userId: String, status: OrderStatus): Result<List<Order>> {
        return try {
            val querySnapshot = ordersCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", status.name)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val orders = querySnapshot.toObjects(Order::class.java)
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> {
        return try {
            val updates = mapOf(
                "status" to status.name,
                "updatedAt" to System.currentTimeMillis()
            )
            
            if (status == OrderStatus.DELIVERED) {
                updates.plus("actualDeliveryTime" to System.currentTimeMillis())
            }
            
            ordersCollection.document(orderId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun cancelOrder(orderId: String, reason: String = ""): Result<Unit> {
        return try {
            val updates = mapOf(
                "status" to OrderStatus.CANCELLED.name,
                "updatedAt" to System.currentTimeMillis(),
                "cancellationReason" to reason
            )
            
            ordersCollection.document(orderId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getActiveOrders(userId: String): Result<List<Order>> {
        return try {
            val activeStatuses = listOf(
                OrderStatus.PENDING.name,
                OrderStatus.CONFIRMED.name,
                OrderStatus.PREPARING.name,
                OrderStatus.OUT_FOR_DELIVERY.name
            )
            
            val querySnapshot = ordersCollection
                .whereEqualTo("userId", userId)
                .whereIn("status", activeStatuses)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val orders = querySnapshot.toObjects(Order::class.java)
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getOrderHistory(userId: String): Result<List<Order>> {
        return try {
            val completedStatuses = listOf(
                OrderStatus.DELIVERED.name,
                OrderStatus.CANCELLED.name,
                OrderStatus.REFUNDED.name
            )
            
            val querySnapshot = ordersCollection
                .whereEqualTo("userId", userId)
                .whereIn("status", completedStatuses)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()
            
            val orders = querySnapshot.toObjects(Order::class.java)
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}