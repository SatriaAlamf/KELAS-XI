package com.example.mediasocial.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediasocial.activities.CreatePostActivity
import com.example.mediasocial.activities.StoryViewerActivity
import com.example.mediasocial.adapters.PostsAdapter
import com.example.mediasocial.adapters.StoriesAdapter
import com.example.mediasocial.databinding.FragmentHomeBinding
import com.example.mediasocial.models.Notification
import com.example.mediasocial.models.NotificationType
import com.example.mediasocial.models.Post
import com.example.mediasocial.models.Story
import com.example.mediasocial.utils.Constants
import com.example.mediasocial.utils.ToastUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    
    private val stories = mutableListOf<Story>()
    private val posts = mutableListOf<Post>()
    
    private lateinit var storiesAdapter: StoriesAdapter
    private lateinit var postsAdapter: PostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        
        setupRecyclerViews()
        setupClickListeners()
        loadStories()
        loadPosts()
    }

    private fun setupRecyclerViews() {
        // Stories RecyclerView
        storiesAdapter = StoriesAdapter(stories) { story ->
            openStoryViewer(story)
        }
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = storiesAdapter
        }

        // Posts RecyclerView
        postsAdapter = PostsAdapter(
            posts,
            onLikeClick = { post, position -> toggleLike(post, position) },
            onCommentClick = { post -> openComments(post) },
            onPostClick = { post -> openPostDetail(post) }
        )
        binding.rvPosts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postsAdapter
        }
    }

    private fun setupClickListeners() {
        binding.fabAddPost.setOnClickListener {
            showCreateContentDialog()
        }
    }
    
    private fun showCreateContentDialog() {
        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
        val dialogView = layoutInflater.inflate(com.example.mediasocial.R.layout.dialog_create_content, null)
        dialog.setView(dialogView)
        
        val alertDialog = dialog.create()
        
        dialogView.findViewById<android.widget.Button>(com.example.mediasocial.R.id.btnCreatePost).setOnClickListener {
            alertDialog.dismiss()
            startActivity(Intent(requireContext(), CreatePostActivity::class.java))
        }
        
        dialogView.findViewById<android.widget.Button>(com.example.mediasocial.R.id.btnCreateStory).setOnClickListener {
            alertDialog.dismiss()
            startActivity(Intent(requireContext(), com.example.mediasocial.activities.CreateStoryActivity::class.java))
        }
        
        dialogView.findViewById<android.widget.Button>(com.example.mediasocial.R.id.btnCancel).setOnClickListener {
            alertDialog.dismiss()
        }
        
        alertDialog.show()
    }

    private fun loadStories() {
        val currentTime = System.currentTimeMillis()
        
        firestore.collection(Constants.COLLECTION_STORIES)
            .whereGreaterThan("expiresAt", currentTime)
            .orderBy("expiresAt", Query.Direction.DESCENDING)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                stories.clear()
                for (document in documents) {
                    val story = document.toObject(Story::class.java)
                    if (!story.isExpired()) {
                        stories.add(story)
                    }
                }
                storiesAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                ToastUtils.showShort(requireContext(), "Gagal memuat stories: ${exception.message}")
            }
    }

    private fun loadPosts() {
        binding.progressBar.visibility = View.VISIBLE
        
        firestore.collection(Constants.COLLECTION_POSTS)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .addOnSuccessListener { documents ->
                binding.progressBar.visibility = View.GONE
                posts.clear()
                
                for (document in documents) {
                    val post = document.toObject(Post::class.java)
                    posts.add(post)
                }
                
                postsAdapter.notifyDataSetChanged()
                
                if (posts.isEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                } else {
                    binding.tvEmptyState.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                binding.progressBar.visibility = View.GONE
                ToastUtils.showShort(requireContext(), "Gagal memuat posts: ${exception.message}")
            }
    }

    private fun toggleLike(post: Post, position: Int) {
        val currentUserId = auth.currentUser?.uid ?: return
        val postRef = firestore.collection(Constants.COLLECTION_POSTS).document(post.postId)
        
        val isLiked = post.isLikedBy(currentUserId)
        val updatedLikes = if (isLiked) {
            post.likes.filterNot { it == currentUserId }
        } else {
            post.likes + currentUserId
        }
        
        val updatedPost = post.copy(
            likes = updatedLikes,
            likesCount = updatedLikes.size
        )
        
        postRef.update(
            mapOf(
                "likes" to updatedLikes,
                "likesCount" to updatedLikes.size
            )
        ).addOnSuccessListener {
            postsAdapter.updatePost(position, updatedPost)
            
            // Create notification if it's a new like
            if (!isLiked) {
                createLikeNotification(post)
            }
        }.addOnFailureListener { exception ->
            ToastUtils.showShort(requireContext(), "Gagal update like: ${exception.message}")
        }
    }

    private fun createLikeNotification(post: Post) {
        val currentUserId = auth.currentUser?.uid ?: return
        if (post.userId == currentUserId) return // Don't notify self
        
        firestore.collection(Constants.COLLECTION_USERS)
            .document(currentUserId)
            .get()
            .addOnSuccessListener { document ->
                val username = document.getString("username") ?: ""
                val profileImage = document.getString("profileImageUrl") ?: ""
                
                val notification = Notification(
                    notificationId = firestore.collection(Constants.COLLECTION_NOTIFICATIONS).document().id,
                    userId = post.userId,
                    fromUserId = currentUserId,
                    fromUsername = username,
                    fromUserProfileImage = profileImage,
                    type = NotificationType.LIKE,
                    postId = post.postId,
                    postImageUrl = post.imageUrl,
                    message = "$username menyukai postingan Anda",
                    timestamp = System.currentTimeMillis(),
                    isRead = false
                )
                
                firestore.collection(Constants.COLLECTION_NOTIFICATIONS)
                    .document(notification.notificationId)
                    .set(notification)
            }
    }

    private fun openStoryViewer(story: Story) {
        val intent = Intent(requireContext(), StoryViewerActivity::class.java)
        intent.putExtra(Constants.EXTRA_STORY_ID, story.storyId)
        startActivity(intent)
    }

    private fun openComments(post: Post) {
        // TODO: Implement comments functionality
        ToastUtils.showShort(requireContext(), "Fitur komentar akan segera hadir")
    }

    private fun openPostDetail(post: Post) {
        // TODO: Implement post detail view
        ToastUtils.showShort(requireContext(), "Detail postingan")
    }

    override fun onResume() {
        super.onResume()
        loadStories()
        loadPosts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
