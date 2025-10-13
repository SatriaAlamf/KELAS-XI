package com.jetpackcompose.foodorderingadmin.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jetpackcompose.foodorderingadmin.models.Order
import com.jetpackcompose.foodorderingadmin.models.OrderStatus
import com.jetpackcompose.foodorderingadmin.models.PaymentStatus
import com.jetpackcompose.foodorderingadmin.utils.Constants
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class OrderRepository {
    
    private val db = FirebaseFirestore.getInstance()
    private val ordersCollection = db.collection(Constants.COLLECTION_ORDERS)
    
    suspend fun getAllOrders(limit: Int = 50): Result<List<Order>> {
        return try {
            val snapshot = ordersCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            val orders = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Order::class.java)?.copy(id = doc.id)
            }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getOrdersByStatus(status: OrderStatus): Result<List<Order>> {
        return try {
            val snapshot = ordersCollection
                .whereEqualTo("status", status.name)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val orders = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Order::class.java)?.copy(id = doc.id)
            }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val document = ordersCollection.document(orderId).get().await()
            val order = document.toObject(Order::class.java)?.copy(id = document.id)
            
            if (order != null) {
                Result.success(order)
            } else {
                Result.failure(Exception("Order not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit> {
        return try {
            val updates = hashMapOf<String, Any>(
                "status" to status.name,
                "updatedAt" to System.currentTimeMillis()
            )
            
            if (status == OrderStatus.DELIVERED) {
                updates["actualDeliveryTime"] = System.currentTimeMillis()
            }
            
            ordersCollection.document(orderId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updatePaymentStatus(orderId: String, paymentStatus: PaymentStatus): Result<Unit> {
        return try {
            ordersCollection.document(orderId)
                .update(mapOf(
                    "paymentStatus" to paymentStatus.name,
                    "updatedAt" to System.currentTimeMillis()
                ))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getTodayOrders(): Result<List<Order>> {
        return try {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.timeInMillis
            
            val snapshot = ordersCollection
                .whereGreaterThanOrEqualTo("createdAt", startOfDay)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val orders = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Order::class.java)?.copy(id = doc.id)
            }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getOrdersInDateRange(startDate: Long, endDate: Long): Result<List<Order>> {
        return try {
            val snapshot = ordersCollection
                .whereGreaterThanOrEqualTo("createdAt", startDate)
                .whereLessThanOrEqualTo("createdAt", endDate)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val orders = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Order::class.java)?.copy(id = doc.id)
            }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
