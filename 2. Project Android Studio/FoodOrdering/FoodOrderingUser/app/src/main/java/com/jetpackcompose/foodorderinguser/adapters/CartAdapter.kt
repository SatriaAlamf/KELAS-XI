package com.jetpackcompose.foodorderinguser.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jetpackcompose.foodorderinguser.databinding.ItemCartBinding
import com.jetpackcompose.foodorderinguser.models.CartItem
import com.jetpackcompose.foodorderinguser.utils.FormatUtils
import com.jetpackcompose.foodorderinguser.utils.ImageLoader

class CartAdapter(
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onItemRemoved: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.tvFoodName.text = cartItem.foodName
            binding.tvPrice.text = FormatUtils.formatPrice(cartItem.price)
            binding.tvTotalPrice.text = FormatUtils.formatPrice(cartItem.getTotalPrice())
            binding.tvQuantity.text = cartItem.quantity.toString()
            
            // Show special instructions if any
            if (cartItem.specialInstructions.isNotEmpty()) {
                binding.tvSpecialInstructions.text = "Note: ${cartItem.specialInstructions}"
                binding.tvSpecialInstructions.visibility = android.view.View.VISIBLE
            } else {
                binding.tvSpecialInstructions.visibility = android.view.View.GONE
            }
            
            // Show selected addons
            if (cartItem.selectedAddons.isNotEmpty()) {
                val addonsText = cartItem.selectedAddons.joinToString(", ") { 
                    "${it.name} (+${FormatUtils.formatPrice(it.price)})"
                }
                binding.tvAddons.text = "Addons: $addonsText"
                binding.tvAddons.visibility = android.view.View.VISIBLE
            } else {
                binding.tvAddons.visibility = android.view.View.GONE
            }
            
            // Load food image
            ImageLoader.loadImage(
                binding.root.context,
                binding.ivFoodImage,
                cartItem.foodImage
            )
            
            // Quantity controls
            binding.btnDecrease.setOnClickListener {
                val newQuantity = cartItem.quantity - 1
                if (newQuantity > 0) {
                    onQuantityChanged(cartItem, newQuantity)
                }
            }
            
            binding.btnIncrease.setOnClickListener {
                val newQuantity = cartItem.quantity + 1
                onQuantityChanged(cartItem, newQuantity)
            }
            
            // Remove item
            binding.btnRemove.setOnClickListener {
                onItemRemoved(cartItem)
            }
        }
    }
}

class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem == newItem
    }
}