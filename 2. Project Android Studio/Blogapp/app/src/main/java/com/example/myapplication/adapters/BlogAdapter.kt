package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemBlogBinding
import com.example.myapplication.models.Blog
import com.example.myapplication.utils.AppUtils

/**
 * Adapter untuk RecyclerView Blog
 * Menggunakan ListAdapter dengan DiffUtil untuk performa yang lebih baik
 */
class BlogAdapter(
    private val onLikeClick: (Blog) -> Unit,
    private val onSaveClick: (Blog) -> Unit,
    private val onShareClick: (Blog) -> Unit,
    private val onBlogClick: (Blog) -> Unit
) : ListAdapter<Blog, BlogAdapter.BlogViewHolder>(BlogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val binding = ItemBlogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blog = getItem(position)
        holder.bind(blog)
    }

    inner class BlogViewHolder(
        private val binding: ItemBlogBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(blog: Blog) {
            binding.apply {
                // Set title dan content
                tvBlogTitle.text = blog.title
                tvBlogContent.text = AppUtils.truncateText(blog.content, 150)

                // Set author info
                tvAuthorName.text = blog.authorName
                tvTimestamp.text = blog.getFormattedDate()

                // Set like count
                tvLikeCount.text = AppUtils.formatNumber(blog.likeCount)

                // Load blog image dengan Glide
                Glide.with(binding.root.context)
                    .load(blog.imageUrl)
                    .placeholder(R.drawable.bg_image_placeholder)
                    .error(R.drawable.ic_image)
                    .centerCrop()
                    .into(ivBlogImage)

                // Load author image (sementara gunakan default icon)
                Glide.with(binding.root.context)
                    .load(R.drawable.ic_profile)
                    .into(ivAuthorImage)

                // Click listeners
                layoutLike.setOnClickListener {
                    onLikeClick(blog)
                }

                layoutSave.setOnClickListener {
                    onSaveClick(blog)
                }

                ivShare.setOnClickListener {
                    onShareClick(blog)
                }

                root.setOnClickListener {
                    onBlogClick(blog)
                }
            }
        }
    }

    /**
     * DiffUtil Callback untuk efisiensi update RecyclerView
     */
    class BlogDiffCallback : DiffUtil.ItemCallback<Blog>() {
        override fun areItemsTheSame(oldItem: Blog, newItem: Blog): Boolean {
            return oldItem.blogId == newItem.blogId
        }

        override fun areContentsTheSame(oldItem: Blog, newItem: Blog): Boolean {
            return oldItem == newItem
        }
    }
}
