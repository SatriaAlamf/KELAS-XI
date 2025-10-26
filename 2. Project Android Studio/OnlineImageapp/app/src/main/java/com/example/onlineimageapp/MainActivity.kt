package com.example.onlineimageapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineimageapp.databinding.ActivityMainBinding
import com.example.onlineimageapp.ui.ImageDetailActivity
import com.example.onlineimageapp.ui.adapter.ImageAdapter
import com.example.onlineimageapp.ui.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var imageAdapter: ImageAdapter
    private var searchJob: Job? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        setupRecyclerView()
        setupSearch()
        setupRefresh()
        setupFab()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        imageAdapter = ImageAdapter(
            onImageClick = { imageItem ->
                val intent = Intent(this, ImageDetailActivity::class.java).apply {
                    putExtra(ImageDetailActivity.EXTRA_IMAGE_URL, imageItem.url)
                    putExtra(ImageDetailActivity.EXTRA_IMAGE_TITLE, imageItem.title)
                    putExtra(ImageDetailActivity.EXTRA_IMAGE_DESCRIPTION, imageItem.description)
                    putExtra(ImageDetailActivity.EXTRA_IMAGE_ID, imageItem.id)
                    putStringArrayListExtra(ImageDetailActivity.EXTRA_IMAGE_TAGS, ArrayList(imageItem.tags))
                }
                startActivity(intent)
            },
            onFavoriteClick = { imageItem ->
                viewModel.toggleFavorite(imageItem)
            }
        )
        
        val spanCount = 2
        val layoutManager = GridLayoutManager(this, spanCount)
        
        binding.recyclerView.apply {
            this.layoutManager = layoutManager
            adapter = imageAdapter
            
            // Infinite scroll
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    
                    val totalItemCount = layoutManager.itemCount
                    val visibleItemCount = layoutManager.childCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    
                    if (!viewModel.isLoading.value!! && 
                        (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 4) {
                        viewModel.loadMoreImages()
                    }
                }
            })
        }
    }
    
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(500) // Debounce
                    val query = s?.toString()?.trim() ?: ""
                    if (query.length > 2 || query.isEmpty()) {
                        viewModel.searchImages(query)
                    }
                }
            }
        })
    }
    
    private fun setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshImages()
        }
    }
    
    private fun setupFab() {
        binding.fabRandom.setOnClickListener {
            viewModel.refreshImages()
        }
    }
    
    private fun observeViewModel() {
        viewModel.images.observe(this) { images ->
            imageAdapter.submitList(images)
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
            binding.progressBar.visibility = if (isLoading && imageAdapter.itemCount == 0) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
        }
        
        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}