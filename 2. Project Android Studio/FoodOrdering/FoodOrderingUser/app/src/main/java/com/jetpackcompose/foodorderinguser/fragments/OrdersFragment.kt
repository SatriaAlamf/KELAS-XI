package com.jetpackcompose.foodorderinguser.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jetpackcompose.foodorderinguser.adapters.OrderAdapter
import com.jetpackcompose.foodorderinguser.databinding.FragmentOrdersBinding
import com.jetpackcompose.foodorderinguser.repository.OrderRepository
import com.jetpackcompose.foodorderinguser.utils.SharedPrefsManager
import kotlinx.coroutines.launch

class OrdersFragment : Fragment() {
    
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var orderRepository: OrderRepository
    private lateinit var sharedPrefsManager: SharedPrefsManager
    private lateinit var orderAdapter: OrderAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        orderRepository = OrderRepository()
        sharedPrefsManager = SharedPrefsManager(requireContext())
        
        setupRecyclerView()
        setupTabs()
        loadActiveOrders()
    }
    
    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter { order ->
            // Handle order click - open order details
            Toast.makeText(requireContext(), "Order ${order.orderNumber} clicked", Toast.LENGTH_SHORT).show()
        }
        
        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
    }
    
    private fun setupTabs() {
        binding.chipActive.setOnClickListener {
            binding.chipActive.isChecked = true
            binding.chipHistory.isChecked = false
            loadActiveOrders()
        }
        
        binding.chipHistory.setOnClickListener {
            binding.chipActive.isChecked = false
            binding.chipHistory.isChecked = true
            loadOrderHistory()
        }
    }
    
    private fun loadActiveOrders() {
        val userId = sharedPrefsManager.getUserId()
        if (userId.isEmpty()) {
            showEmptyOrders("Please login to view your orders")
            return
        }
        
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            orderRepository.getActiveOrders(userId)
                .onSuccess { orders ->
                    binding.progressBar.visibility = View.GONE
                    if (orders.isEmpty()) {
                        showEmptyOrders("No active orders")
                    } else {
                        showOrders(orders)
                    }
                }
                .onFailure { error ->
                    binding.progressBar.visibility = View.GONE
                    showEmptyOrders("Error loading orders: ${error.message}")
                }
        }
    }
    
    private fun loadOrderHistory() {
        val userId = sharedPrefsManager.getUserId()
        if (userId.isEmpty()) {
            showEmptyOrders("Please login to view your orders")
            return
        }
        
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            orderRepository.getOrderHistory(userId)
                .onSuccess { orders ->
                    binding.progressBar.visibility = View.GONE
                    if (orders.isEmpty()) {
                        showEmptyOrders("No order history")
                    } else {
                        showOrders(orders)
                    }
                }
                .onFailure { error ->
                    binding.progressBar.visibility = View.GONE
                    showEmptyOrders("Error loading order history: ${error.message}")
                }
        }
    }
    
    private fun showEmptyOrders(message: String) {
        binding.layoutEmptyOrders.visibility = View.VISIBLE
        binding.rvOrders.visibility = View.GONE
        binding.tvEmptyMessage.text = message
    }
    
    private fun showOrders(orders: List<com.jetpackcompose.foodorderinguser.models.Order>) {
        binding.layoutEmptyOrders.visibility = View.GONE
        binding.rvOrders.visibility = View.VISIBLE
        orderAdapter.submitList(orders)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}