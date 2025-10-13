package com.jetpackcompose.foodorderingadmin.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jetpackcompose.foodorderingadmin.R
import com.jetpackcompose.foodorderingadmin.databinding.ItemOrderBinding
import com.jetpackcompose.foodorderingadmin.models.Order
import com.jetpackcompose.foodorderingadmin.models.OrderStatus
import com.jetpackcompose.foodorderingadmin.utils.FormatUtils

class OrderAdapter(
    private val onOrderClick: (Order) -> Unit,
    private val onUpdateStatusClick: (Order) -> Unit
) : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                tvOrderNumber.text = order.orderNumber
                tvCustomerName.text = order.userName
                tvCustomerPhone.text = order.userPhone
                tvTotalAmount.text = FormatUtils.formatPrice(order.total)
                tvItemCount.text = "${order.items.size} items"
                tvOrderTime.text = FormatUtils.formatTime(order.createdAt)
                
                // Set status
                tvOrderStatus.text = order.status.displayName
                tvOrderStatus.setBackgroundColor(getStatusColor(order.status))
                
                // Click listeners
                root.setOnClickListener { onOrderClick(order) }
                btnViewDetails.setOnClickListener { onOrderClick(order) }
                btnUpdateStatus.setOnClickListener { onUpdateStatusClick(order) }
            }
        }

        private fun getStatusColor(status: OrderStatus): Int {
            return when (status) {
                OrderStatus.PENDING -> Color.parseColor("#FF9800")
                OrderStatus.CONFIRMED -> Color.parseColor("#2196F3")
                OrderStatus.PREPARING -> Color.parseColor("#9C27B0")
                OrderStatus.OUT_FOR_DELIVERY -> Color.parseColor("#FF5722")
                OrderStatus.DELIVERED -> Color.parseColor("#4CAF50")
                OrderStatus.CANCELLED -> Color.parseColor("#F44336")
                OrderStatus.REFUNDED -> Color.parseColor("#757575")
            }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}
