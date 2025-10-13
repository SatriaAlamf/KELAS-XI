package com.jetpackcompose.foodorderinguser.models

data class CartItem(
    val id: String = "",
    val foodId: String = "",
    val foodName: String = "",
    val foodImage: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val specialInstructions: String = "",
    val selectedAddons: List<Addon> = emptyList(),
    val userId: String = ""
) {
    fun getTotalPrice(): Double {
        val addonPrice = selectedAddons.sumOf { it.price }
        return (price + addonPrice) * quantity
    }
}

data class Addon(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val isSelected: Boolean = false
)

data class Cart(
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val tax: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun calculateTotals(): Cart {
        val subtotal = items.sumOf { it.getTotalPrice() }
        val tax = subtotal * 0.1 // 10% tax
        val total = subtotal + deliveryFee + tax - discount
        
        return this.copy(
            subtotal = subtotal,
            tax = tax,
            total = total
        )
    }
}