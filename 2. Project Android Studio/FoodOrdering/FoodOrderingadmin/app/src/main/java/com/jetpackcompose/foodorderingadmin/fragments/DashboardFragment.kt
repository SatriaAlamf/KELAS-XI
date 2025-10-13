package com.jetpackcompose.foodorderingadmin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jetpackcompose.foodorderingadmin.adapters.OrderAdapter
import com.jetpackcompose.foodorderingadmin.databinding.FragmentDashboardBinding
import com.jetpackcompose.foodorderingadmin.models.DashboardStats
import com.jetpackcompose.foodorderingadmin.repository.FoodRepository
import com.jetpackcompose.foodorderingadmin.repository.OrderRepository
import com.jetpackcompose.foodorderingadmin.utils.FormatUtils
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private val orderRepository = OrderRepository()
    private val foodRepository = FoodRepository()
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSwipeRefresh()
        loadDashboardData()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            onOrderClick = { /* Navigate to order details */ },
            onUpdateStatusClick = { /* Show status update dialog */ }
        )
        
        binding.rvRecentOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadDashboardData()
        }
    }

    private fun loadDashboardData() {
        binding.swipeRefresh.isRefreshing = true
        
        lifecycleScope.launch {
            // Load today's orders
            val todayOrdersResult = orderRepository.getTodayOrders()
            
            todayOrdersResult.onSuccess { orders ->
                val todayOrders = orders.size
                val todayRevenue = orders.filter { 
                    it.status.name != "CANCELLED" 
                }.sumOf { it.total }
                val pendingOrders = orders.filter { 
                    it.status.name == "PENDING" || it.status.name == "CONFIRMED" 
                }.size
                
                // Update stats
                binding.tvTodayOrders.text = todayOrders.toString()
                binding.tvTodayRevenue.text = FormatUtils.formatPrice(todayRevenue)
                binding.tvPendingOrders.text = pendingOrders.toString()
                
                // Show recent orders (limited to 5)
                orderAdapter.submitList(orders.take(5))
            }
            
            // Load total products
            val foodsResult = foodRepository.getAllFoods()
            foodsResult.onSuccess { foods ->
                binding.tvTotalProducts.text = foods.size.toString()
            }
            
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
