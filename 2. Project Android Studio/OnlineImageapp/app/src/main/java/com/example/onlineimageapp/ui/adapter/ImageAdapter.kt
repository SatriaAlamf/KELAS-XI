package com.example.onlineimageapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.onlineimageapp.R
import com.example.onlineimageapp.data.model.ImageItem
import com.example.onlineimageapp.databinding.ItemImageBinding

class ImageAdapter(
    private val onImageClick: (ImageItem) -> Unit,
    private val onFavoriteClick: (ImageItem) -> Unit
) : ListAdapter<ImageItem, ImageAdapter.ImageViewHolder>(ImageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = getItem(position)
        holder.bind(image)
    }

    inner class ImageViewHolder(
        private val binding: ItemImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(image: ImageItem) {
            binding.apply {
                tvTitle.text = image.title
                tvDescription.text = image.description
                
                // Load image with Glide
                Glide.with(imageView.context)
                    .load(image.thumbnail)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_error)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView)
                
                // Set favorite icon
                ivFavorite.setImageResource(
                    if (image.isFavorite) R.drawable.ic_favorite 
                    else R.drawable.ic_favorite_border
                )
                
                // Click listeners
                root.setOnClickListener {
                    onImageClick(image)
                }
                
                ivFavorite.setOnClickListener {
                    onFavoriteClick(image)
                }
            }
        }
    }

    class ImageDiffCallback : DiffUtil.ItemCallback<ImageItem>() {
        override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
            return oldItem == newItem
        }
    }
}