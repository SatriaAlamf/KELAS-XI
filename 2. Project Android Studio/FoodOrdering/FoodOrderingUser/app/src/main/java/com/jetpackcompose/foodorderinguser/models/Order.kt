package com.jetpackcompose.foodorderinguser.models

data class Order(
    val id: String = "",
    val userId: String = "",
    val orderNumber: String = "",
    val items: List<OrderItem> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val tax: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val deliveryAddress: Address = Address(),
    val estimatedDeliveryTime: Long = 0,
    val actualDeliveryTime: Long? = null,
    val specialInstructions: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class OrderItem(
    val foodId: String = "",
    val foodName: String = "",
    val foodImage: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val specialInstructions: String = "",
    val selectedAddons: List<Addon> = emptyList()
) {
    fun getTotalPrice(): Double {
        val addonPrice = selectedAddons.sumOf { it.price }
        return (price + addonPrice) * quantity
    }
}

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
    REFUNDED
}

enum class PaymentMethod {
    CASH_ON_DELIVERY,
    CREDIT_CARD,
    DEBIT_CARD,
    DIGITAL_WALLET,
    UPI
}

enum class PaymentStatus {
    PENDING,
    PAID,
    FAILED,
    REFUNDED
}