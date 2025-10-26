package com.example.newsapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupWebView()
        setupClickListeners()
        setupToolbar()
        setupSwipeRefresh()
        
        // Load default URL
        loadUrl(binding.etUrl.text.toString())
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.webView.reload()
        }
        
        // Set custom colors for refresh indicator
        binding.swipeRefresh.setColorSchemeResources(
            R.color.primary_blue,
            R.color.accent_orange,
            R.color.primary_blue_dark
        )
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        with(binding.webView) {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                loadWithOverviewMode = true
                useWideViewPort = true
                allowContentAccess = true
                allowFileAccess = true
                javaScriptCanOpenWindowsAutomatically = true
                mediaPlaybackRequiresUserGesture = false
                
                // Enable mixed content for HTTP/HTTPS
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            }
            
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvStatus.text = "Loading..."
                    updateNavigationButtons()
                }
                
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    binding.tvStatus.text = "Loaded"
                    binding.tvLoadingProgress.text = ""
                    updateNavigationButtons()
                    
                    // Update URL in EditText
                    url?.let { binding.etUrl.setText(it) }
                }
                
                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    super.onReceivedError(view, request, error)
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    binding.tvStatus.text = "Error loading page"
                    Toast.makeText(this@MainActivity, "Failed to load page", Toast.LENGTH_SHORT).show()
                }
            }
            
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    binding.tvLoadingProgress.text = "$newProgress%"
                    if (newProgress == 100) {
                        binding.progressBar.visibility = View.GONE
                        binding.tvLoadingProgress.text = ""
                    } else {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
                
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    supportActionBar?.title = title ?: "News App"
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnGo.setOnClickListener {
            val url = binding.etUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                loadUrl(url)
            } else {
                Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnBack.setOnClickListener {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                Toast.makeText(this, "No previous page", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnForward.setOnClickListener {
            if (binding.webView.canGoForward()) {
                binding.webView.goForward()
            } else {
                Toast.makeText(this, "No next page", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnRefresh.setOnClickListener {
            binding.webView.reload()
        }
        
        // Quick access chips
        binding.chipDetik.setOnClickListener {
            loadUrl("https://www.detik.com")
        }
        
        binding.chipKompas.setOnClickListener {
            loadUrl("https://www.kompas.com")
        }
        
        binding.chipCNN.setOnClickListener {
            loadUrl("https://edition.cnn.com")
        }
        
        binding.chipBBC.setOnClickListener {
            loadUrl("https://www.bbc.com/news")
        }
    }
    
    private fun loadUrl(url: String) {
        var finalUrl = url
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            finalUrl = "https://$url"
        }
        binding.webView.loadUrl(finalUrl)
        binding.etUrl.setText(finalUrl)
    }
    
    private fun updateNavigationButtons() {
        binding.btnBack.isEnabled = binding.webView.canGoBack()
        binding.btnForward.isEnabled = binding.webView.canGoForward()
    }
    
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_bookmark -> {
                val url = binding.webView.url ?: ""
                val title = binding.webView.title ?: "Bookmark"
                Toast.makeText(this, "Bookmark added: $title", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_share -> {
                shareCurrentPage()
                true
            }
            R.id.action_settings -> {
                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_clear_cache -> {
                clearWebViewCache()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun shareCurrentPage() {
        val url = binding.webView.url ?: ""
        val title = binding.webView.title ?: "Check out this page"
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "$title\n$url")
            putExtra(Intent.EXTRA_SUBJECT, title)
        }
        
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
    
    private fun clearWebViewCache() {
        binding.webView.clearCache(true)
        binding.webView.clearHistory()
        WebStorage.getInstance().deleteAllData()
        Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroy() {
        binding.webView.destroy()
        super.onDestroy()
    }
}