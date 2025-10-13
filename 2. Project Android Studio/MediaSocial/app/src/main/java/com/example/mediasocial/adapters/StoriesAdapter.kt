package com.example.mediasocial.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediasocial.databinding.ItemStoryBinding
import com.example.mediasocial.models.Story
import com.example.mediasocial.utils.ImageLoader

class StoriesAdapter(
    private val stories: List<Story>,
    private val onStoryClick: (Story) -> Unit
) : RecyclerView.Adapter<StoriesAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: Story) {
            binding.apply {
                tvUsername.text = story.username
                ImageLoader.loadProfileImage(ivProfile, story.userProfileImage)
                
                // Show or hide story border based on viewed status
                // You would need to check if current user has viewed this story
                // For now, we'll show the border for all unviewed stories
                
                root.setOnClickListener {
                    onStoryClick(story)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    override fun getItemCount(): Int = stories.size
}
