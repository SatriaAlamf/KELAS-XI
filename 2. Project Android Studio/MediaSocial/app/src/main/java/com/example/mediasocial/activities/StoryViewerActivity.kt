package com.example.mediasocial.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.example.mediasocial.databinding.ActivityStoryViewerBinding
import com.example.mediasocial.models.Story
import com.example.mediasocial.utils.Constants
import com.example.mediasocial.utils.DateUtils
import com.example.mediasocial.utils.ImageLoader
import com.example.mediasocial.utils.ToastUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StoryViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryViewerBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var gestureDetector: GestureDetectorCompat

    private var story: Story? = null
    private val handler = Handler(Looper.getMainLooper())
    private var progressRunnable: Runnable? = null
    private var currentProgress = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupGestureDetector()
        loadStory()
        setupClickListeners()
    }

    private fun setupGestureDetector() {
        gestureDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                // Tap on right side goes to next story
                // Tap on left side goes to previous story
                val screenWidth = binding.root.width
                if (e.x > screenWidth / 2) {
                    // Next story (for now just close)
                    finish()
                } else {
                    // Previous story (for now just close)
                    finish()
                }
                return true
            }

            override fun onDown(e: MotionEvent): Boolean {
                pauseProgress()
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                pauseProgress()
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            resumeProgress()
        }
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private fun loadStory() {
        val storyId = intent.getStringExtra(Constants.EXTRA_STORY_ID) ?: run {
            finish()
            return
        }

        firestore.collection(Constants.COLLECTION_STORIES)
            .document(storyId)
            .get()
            .addOnSuccessListener { document ->
                story = document.toObject(Story::class.java)
                story?.let { displayStory(it) }
            }
            .addOnFailureListener { exception ->
                ToastUtils.showShort(this, "Gagal memuat story: ${exception.message}")
                finish()
            }
    }

    private fun displayStory(story: Story) {
        binding.apply {
            tvUsername.text = story.username
            tvTimestamp.text = DateUtils.getTimeAgo(story.timestamp)
            ImageLoader.loadProfileImage(ivUserProfile, story.userProfileImage)
            ImageLoader.loadStoryImage(ivStoryImage, story.imageUrl)
        }

        markStoryAsViewed(story)
        startProgress()
    }

    private fun markStoryAsViewed(story: Story) {
        val currentUserId = auth.currentUser?.uid ?: return
        
        // Don't mark own stories as viewed
        if (story.userId == currentUserId) return

        // Check if already viewed
        if (story.isViewedBy(currentUserId)) return

        val updatedViewedBy = story.viewedBy + currentUserId

        firestore.collection(Constants.COLLECTION_STORIES)
            .document(story.storyId)
            .update("viewedBy", updatedViewedBy)
    }

    private fun startProgress() {
        currentProgress = 0
        binding.storyProgressBar.progress = 0

        progressRunnable = object : Runnable {
            override fun run() {
                currentProgress += 100
                val progress = (currentProgress * 100 / Constants.STORY_DURATION_MS).toInt()
                binding.storyProgressBar.progress = progress

                if (currentProgress < Constants.STORY_DURATION_MS) {
                    handler.postDelayed(this, 100)
                } else {
                    // Story finished, move to next or close
                    finish()
                }
            }
        }
        handler.post(progressRunnable!!)
    }

    private fun pauseProgress() {
        progressRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun resumeProgress() {
        progressRunnable?.let { handler.post(it) }
    }

    private fun setupClickListeners() {
        binding.ivClose.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        pauseProgress()
    }

    override fun onResume() {
        super.onResume()
        if (story != null) {
            resumeProgress()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        progressRunnable?.let { handler.removeCallbacks(it) }
    }
}
