package com.example.mediasocial.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mediasocial.R
import com.example.mediasocial.databinding.ItemPostBinding
import com.example.mediasocial.models.Post
import com.example.mediasocial.utils.DateUtils
import com.example.mediasocial.utils.ImageLoader
import com.google.firebase.auth.FirebaseAuth

class PostsAdapter(
    private val posts: MutableList<Post>,
    private val onLikeClick: (Post, Int) -> Unit,
    private val onCommentClick: (Post) -> Unit,
    private val onPostClick: (Post) -> Unit
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post, position: Int) {
            binding.apply {
                // User info
                tvUsername.text = post.username
                tvTimestamp.text = DateUtils.getTimeAgo(post.timestamp)
                ImageLoader.loadProfileImage(ivUserProfile, post.userProfileImage)

                // Post image
                ImageLoader.loadImage(ivPostImage, post.imageUrl)

                // Description
                tvDescription.text = post.description

                // Likes count
                val likesText = if (post.likesCount == 1) {
                    "1 like"
                } else {
                    "${post.likesCount} likes"
                }
                tvLikesCount.text = likesText

                // Like button state
                val isLiked = post.isLikedBy(currentUserId)
                if (isLiked) {
                    ivLike.setImageResource(R.drawable.ic_favorite)
                    ivLike.setColorFilter(
                        ContextCompat.getColor(binding.root.context, R.color.like_red)
                    )
                } else {
                    ivLike.setImageResource(R.drawable.ic_favorite_border)
                    ivLike.setColorFilter(
                        ContextCompat.getColor(binding.root.context, R.color.text_primary)
                    )
                }

                // Click listeners
                ivLike.setOnClickListener {
                    onLikeClick(post, position)
                }

                ivComment.setOnClickListener {
                    onCommentClick(post)
                }

                root.setOnClickListener {
                    onPostClick(post)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position], position)
    }

    override fun getItemCount(): Int = posts.size

    fun updatePost(position: Int, post: Post) {
        posts[position] = post
        notifyItemChanged(position)
    }
}
