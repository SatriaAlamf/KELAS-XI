package com.jetpackcompose.foodorderingadmin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jetpackcompose.foodorderingadmin.databinding.ItemFoodBinding
import com.jetpackcompose.foodorderingadmin.models.Food
import com.jetpackcompose.foodorderingadmin.utils.FormatUtils
import com.jetpackcompose.foodorderingadmin.utils.ImageLoader

class FoodAdapter(
    private val onEditClick: (Food) -> Unit,
    private val onDeleteClick: (Food) -> Unit,
    private val onAvailabilityChanged: (Food, Boolean) -> Unit
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
            binding.apply {
                tvFoodName.text = food.name
                tvCategoryName.text = food.categoryName
                
                val finalPrice = if (food.discount > 0) {
                    food.price * (1 - food.discount / 100.0)
                } else {
                    food.price
                }
                tvPrice.text = FormatUtils.formatPrice(finalPrice)
                
                switchAvailable.isChecked = food.isAvailable
                
                // Load image
                ImageLoader.loadImage(
                    context = itemView.context,
                    url = food.imageUrl,
                    imageView = ivFoodImage
                )
                
                // Click listeners
                btnEdit.setOnClickListener { onEditClick(food) }
                btnDelete.setOnClickListener { onDeleteClick(food) }
                
                switchAvailable.setOnCheckedChangeListener { _, isChecked ->
                    onAvailabilityChanged(food, isChecked)
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
}
