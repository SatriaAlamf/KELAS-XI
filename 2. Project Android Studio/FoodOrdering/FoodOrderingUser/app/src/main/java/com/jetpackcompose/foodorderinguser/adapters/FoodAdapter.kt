package com.jetpackcompose.foodorderinguser.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jetpackcompose.foodorderinguser.databinding.ItemFoodBinding
import com.jetpackcompose.foodorderinguser.models.Food
import com.jetpackcompose.foodorderinguser.utils.FormatUtils
import com.jetpackcompose.foodorderinguser.utils.ImageLoader

class FoodAdapter(
    private val onFoodClick: (Food) -> Unit
) : ListAdapter<Food, FoodAdapter.FoodViewHolder>(FoodDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FoodViewHolder(
        private val binding: ItemFoodBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(food: Food) {
            binding.tvFoodName.text = food.name
            binding.tvFoodDescription.text = food.description
            binding.tvPrice.text = FormatUtils.formatPrice(food.price)
            
            // Show rating and review count
            binding.tvRating.text = FormatUtils.formatRating(food.rating)
            binding.tvReviewCount.text = "(${food.reviewCount})"
            
            // Show preparation time
            binding.tvPrepTime.text = FormatUtils.formatPreparationTime(food.preparationTime)
            
            // Handle discount
            if (food.discount > 0) {
                binding.tvOriginalPrice.visibility = View.VISIBLE
                binding.tvDiscount.visibility = View.VISIBLE
                
                binding.tvOriginalPrice.text = FormatUtils.formatPrice(food.originalPrice)
                binding.tvOriginalPrice.paintFlags = binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvDiscount.text = "${food.discount}% OFF"
            } else {
                binding.tvOriginalPrice.visibility = View.GONE
                binding.tvDiscount.visibility = View.GONE
            }
            
            // Show food indicators
            binding.ivVeg.visibility = if (food.isVegetarian) View.VISIBLE else View.GONE
            binding.ivSpicy.visibility = if (food.isSpicy) View.VISIBLE else View.GONE
            
            // Load food image
            ImageLoader.loadImage(
                binding.root.context,
                binding.ivFoodImage,
                food.imageUrl
            )
            
            binding.root.setOnClickListener {
                onFoodClick(food)
            }
        }
    }
}

class FoodDiffCallback : DiffUtil.ItemCallback<Food>() {
    override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
        return oldItem == newItem
    }
}