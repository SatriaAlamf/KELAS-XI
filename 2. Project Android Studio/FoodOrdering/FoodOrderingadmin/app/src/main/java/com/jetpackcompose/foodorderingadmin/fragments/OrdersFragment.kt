package com.jetpackcompose.foodorderingadmin.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.jetpackcompose.foodorderingadmin.R
import com.jetpackcompose.foodorderingadmin.adapters.OrderAdapter
import com.jetpackcompose.foodorderingadmin.databinding.FragmentOrdersBinding
import com.jetpackcompose.foodorderingadmin.models.Order
import com.jetpackcompose.foodorderingadmin.models.OrderStatus
import com.jetpackcompose.foodorderingadmin.repository.OrderRepository
import kotlinx.coroutines.launch

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    
    private val orderRepository = OrderRepository()
    private lateinit var orderAdapter: OrderAdapter
    private var allOrders = listOf<Order>()
    private var currentFilter: OrderStatus? = null

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
        
        setupRecyclerView()
        setupSwipeRefresh()
        setupFilterChips()
        loadOrders()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            onOrderClick = { order ->
                // Navigate to order details
                showOrderDetails(order)
            },
            onUpdateStatusClick = { order ->
                showStatusUpdateDialog(order)
            }
        )
        
        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadOrders()
        }
    }

    private fun setupFilterChips() {
        binding.chipGroupStatus.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) {
                currentFilter = null
                filterOrders(null)
                return@setOnCheckedStateChangeListener
            }
            
            val checkedChip = group.findViewById<Chip>(checkedIds[0])
            currentFilter = when (checkedChip.id) {
                R.id.chipPending -> OrderStatus.PENDING
                R.id.chipConfirmed -> OrderStatus.CONFIRMED
                R.id.chipPreparing -> OrderStatus.PREPARING
                R.id.chipDelivery -> OrderStatus.OUT_FOR_DELIVERY
                R.id.chipDelivered -> OrderStatus.DELIVERED
                else -> null
            }
            filterOrders(currentFilter)
        }
    }

    private fun loadOrders() {
        binding.swipeRefresh.isRefreshing = true
        
        lifecycleScope.launch {
            val result = orderRepository.getAllOrders()
            
            result.onSuccess { orders ->
                allOrders = orders
                filterOrders(currentFilter)
            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to load orders: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun filterOrders(status: OrderStatus?) {
        val filteredOrders = if (status == null) {
            allOrders
        } else {
            allOrders.filter { it.status == status }
        }
        orderAdapter.submitList(filteredOrders)
    }

    private fun showOrderDetails(order: Order) {
        val message = buildString {
            append("Order Number: ${order.orderNumber}\n\n")
            append("Customer: ${order.userName}\n")
            append("Phone: ${order.userPhone}\n\n")
            append("Items:\n")
            order.items.forEach { item ->
                append("- ${item.foodName} x${item.quantity}\n")
            }
            append("\nTotal: Rp ${String.format("%,.0f", order.total)}")
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle("Order Details")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showStatusUpdateDialog(order: Order) {
        val statuses = OrderStatus.values().map { it.displayName }.toTypedArray()
        val currentIndex = OrderStatus.values().indexOf(order.status)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Update Order Status")
            .setSingleChoiceItems(statuses, currentIndex) { dialog, which ->
                val newStatus = OrderStatus.values()[which]
                updateOrderStatus(order.id, newStatus)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        lifecycleScope.launch {
            val result = orderRepository.updateOrderStatus(orderId, newStatus)
            
            result.onSuccess {
                Toast.makeText(
                    requireContext(),
                    "Order status updated to ${newStatus.displayName}",
                    Toast.LENGTH_SHORT
                ).show()
                loadOrders()
            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to update status: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
