package com.jetpackcompose.foodorderinguser.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jetpackcompose.foodorderinguser.R
import com.jetpackcompose.foodorderinguser.databinding.ItemOrderBinding
import com.jetpackcompose.foodorderinguser.models.Order
import com.jetpackcompose.foodorderinguser.models.OrderStatus
import com.jetpackcompose.foodorderinguser.utils.Constants
import com.jetpackcompose.foodorderinguser.utils.FormatUtils

class OrderAdapter(
    private val onOrderClick: (Order) -> Unit
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
            binding.tvOrderNumber.text = FormatUtils.formatOrderNumber(order.orderNumber)
            binding.tvOrderDate.text = FormatUtils.formatDateTime(order.createdAt)
            binding.tvTotal.text = FormatUtils.formatPrice(order.total)
            binding.tvItemCount.text = "${order.items.size} items"
            
            // Set status
            binding.tvStatus.text = Constants.ORDER_STATUS_MESSAGES[order.status.name] ?: order.status.name
            
            // Set status color
            val statusColor = when (order.status) {
                OrderStatus.PENDING -> ContextCompat.getColor(binding.root.context, R.color.status_pending)
                OrderStatus.CONFIRMED -> ContextCompat.getColor(binding.root.context, R.color.status_confirmed)
                OrderStatus.PREPARING -> ContextCompat.getColor(binding.root.context, R.color.status_preparing)
                OrderStatus.OUT_FOR_DELIVERY -> ContextCompat.getColor(binding.root.context, R.color.status_out_for_delivery)
                OrderStatus.DELIVERED -> ContextCompat.getColor(binding.root.context, R.color.status_delivered)
                OrderStatus.CANCELLED -> ContextCompat.getColor(binding.root.context, R.color.status_cancelled)
                OrderStatus.REFUNDED -> ContextCompat.getColor(binding.root.context, R.color.status_refunded)
            }
            binding.tvStatus.setTextColor(statusColor)
            
            // Show delivery address
            binding.tvDeliveryAddress.text = order.deliveryAddress.fullAddress
            
            // Show first few items
            val itemsText = order.items.take(2).joinToString(", ") { "${it.quantity}x ${it.foodName}" }
            val moreItemsText = if (order.items.size > 2) " and ${order.items.size - 2} more" else ""
            binding.tvItems.text = itemsText + moreItemsText
            
            binding.root.setOnClickListener {
                onOrderClick(order)
            }
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