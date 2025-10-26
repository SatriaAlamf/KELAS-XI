package com.example.onlineimageapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineimageapp.data.model.ImageItem
import com.example.onlineimageapp.data.repository.ImageRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    
    private val repository = ImageRepository()
    
    private val _images = MutableLiveData<List<ImageItem>>()
    val images: LiveData<List<ImageItem>> = _images
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    private val _favoriteImages = MutableLiveData<Set<String>>(emptySet())
    val favoriteImages: LiveData<Set<String>> = _favoriteImages
    
    private var currentQuery: String = ""
    private var currentPage: Int = 1
    private val allImages = mutableListOf<ImageItem>()
    
    init {
        loadImages()
    }
    
    fun loadImages(refresh: Boolean = false) {
        if (refresh) {
            currentPage = 1
            allImages.clear()
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            
            try {
                val result = if (currentQuery.isBlank()) {
                    repository.getLatestImages(currentPage)
                } else {
                    repository.searchImages(currentQuery, currentPage)
                }
                
                result.fold(
                    onSuccess = { newImages ->
                        if (refresh) {
                            allImages.clear()
                        }
                        allImages.addAll(newImages)
                        _images.value = allImages.toList()
                        currentPage++
                    },
                    onFailure = { exception ->
                        // Fallback to dummy data if API fails
                        if (currentPage == 1) {
                            val dummyImages = repository.getDummyImages()
                            allImages.addAll(dummyImages)
                            _images.value = allImages.toList()
                        }
                        _errorMessage.value = exception.message ?: "Unknown error occurred"
                    }
                )
            } catch (e: Exception) {
                if (currentPage == 1) {
                    val dummyImages = repository.getDummyImages()
                    allImages.addAll(dummyImages)
                    _images.value = allImages.toList()
                }
                _errorMessage.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun searchImages(query: String) {
        currentQuery = query
        currentPage = 1
        allImages.clear()
        loadImages()
    }
    
    fun loadMoreImages() {
        if (_isLoading.value == true) return
        loadImages()
    }
    
    fun toggleFavorite(imageItem: ImageItem) {
        val currentFavorites = _favoriteImages.value?.toMutableSet() ?: mutableSetOf()
        
        if (imageItem.id in currentFavorites) {
            currentFavorites.remove(imageItem.id)
        } else {
            currentFavorites.add(imageItem.id)
        }
        
        _favoriteImages.value = currentFavorites
        
        // Update the image list to reflect favorite changes
        val updatedImages = allImages.map { image ->
            if (image.id == imageItem.id) {
                image.copy(isFavorite = imageItem.id in currentFavorites)
            } else {
                image.copy(isFavorite = image.id in currentFavorites)
            }
        }
        allImages.clear()
        allImages.addAll(updatedImages)
        _images.value = allImages.toList()
    }
    
    fun refreshImages() {
        loadImages(refresh = true)
    }
}