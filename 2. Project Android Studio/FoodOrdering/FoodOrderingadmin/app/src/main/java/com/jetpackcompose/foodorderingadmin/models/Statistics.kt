package com.jetpackcompose.foodorderingadmin.models

data class DashboardStats(
    val todayOrders: Int = 0,
    val todayRevenue: Double = 0.0,
    val pendingOrders: Int = 0,
    val totalCustomers: Int = 0,
    val totalProducts: Int = 0,
    val lowStockItems: Int = 0,
    val weeklyRevenue: List<Double> = emptyList(),
    val monthlyRevenue: Double = 0.0,
    val popularItems: List<PopularItem> = emptyList(),
    val recentOrders: List<Order> = emptyList()
)

data class PopularItem(
    val foodId: String = "",
    val foodName: String = "",
    val imageUrl: String = "",
    val orderCount: Int = 0,
    val revenue: Double = 0.0
)

data class SalesReport(
    val date: String = "",
    val totalOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val averageOrderValue: Double = 0.0,
    val cancelledOrders: Int = 0
)
