package com.jetpackcompose.foodorderingadmin.models

data class Order(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhone: String = "",
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

data class Address(
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val postalCode: String = "",
    val country: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val fullAddress: String = ""
)

enum class OrderStatus(val displayName: String) {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    PREPARING("Preparing"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled"),
    REFUNDED("Refunded");
    
    companion object {
        fun fromString(value: String): OrderStatus {
            return values().find { it.name == value } ?: PENDING
        }
    }
}

enum class PaymentMethod(val displayName: String) {
    CASH_ON_DELIVERY("Cash on Delivery"),
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    DIGITAL_WALLET("Digital Wallet"),
    UPI("UPI");
    
    companion object {
        fun fromString(value: String): PaymentMethod {
            return values().find { it.name == value } ?: CASH_ON_DELIVERY
        }
    }
}

enum class PaymentStatus(val displayName: String) {
    PENDING("Pending"),
    PAID("Paid"),
    FAILED("Failed"),
    REFUNDED("Refunded");
    
    companion object {
        fun fromString(value: String): PaymentStatus {
            return values().find { it.name == value } ?: PENDING
        }
    }
}
