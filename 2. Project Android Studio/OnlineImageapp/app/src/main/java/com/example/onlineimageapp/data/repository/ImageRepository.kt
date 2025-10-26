package com.example.onlineimageapp.data.repository

import com.example.onlineimageapp.data.model.ImageItem
import com.example.onlineimageapp.data.model.UnsplashPhoto
import com.example.onlineimageapp.data.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageRepository {
    
    private val apiService = ApiClient.unsplashApiService
    
    suspend fun searchImages(
        query: String, 
        page: Int = 1, 
        perPage: Int = 20
    ): Result<List<ImageItem>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.searchPhotos(query, page, perPage)
            if (response.isSuccessful) {
                val photos = response.body()?.results ?: emptyList()
                Result.success(photos.map { it.toImageItem() })
            } else {
                Result.failure(Exception("Failed to search images: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getLatestImages(
        page: Int = 1, 
        perPage: Int = 20
    ): Result<List<ImageItem>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPhotos(page, perPage, "latest")
            if (response.isSuccessful) {
                val photos = response.body() ?: emptyList()
                Result.success(photos.map { it.toImageItem() })
            } else {
                Result.failure(Exception("Failed to get images: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getImageById(id: String): Result<ImageItem> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPhoto(id)
            if (response.isSuccessful) {
                val photo = response.body()
                if (photo != null) {
                    Result.success(photo.toImageItem())
                } else {
                    Result.failure(Exception("Image not found"))
                }
            } else {
                Result.failure(Exception("Failed to get image: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Dummy data untuk demo jika API tidak tersedia
    fun getDummyImages(): List<ImageItem> {
        return listOf(
            ImageItem(
                id = "1",
                url = "https://picsum.photos/800/600?random=1",
                title = "Beautiful Landscape",
                description = "A stunning landscape photography",
                width = 800,
                height = 600,
                thumbnail = "https://picsum.photos/400/300?random=1",
                tags = listOf("landscape", "nature", "photography")
            ),
            ImageItem(
                id = "2",
                url = "https://picsum.photos/800/600?random=2",
                title = "Urban Architecture",
                description = "Modern city building design",
                width = 800,
                height = 600,
                thumbnail = "https://picsum.photos/400/300?random=2",
                tags = listOf("architecture", "urban", "building")
            ),
            ImageItem(
                id = "3",
                url = "https://picsum.photos/800/600?random=3",
                title = "Ocean View",
                description = "Peaceful ocean waves",
                width = 800,
                height = 600,
                thumbnail = "https://picsum.photos/400/300?random=3",
                tags = listOf("ocean", "beach", "waves")
            ),
            ImageItem(
                id = "4",
                url = "https://picsum.photos/800/600?random=4",
                title = "Forest Path",
                description = "Walking through the forest",
                width = 800,
                height = 600,
                thumbnail = "https://picsum.photos/400/300?random=4",
                tags = listOf("forest", "nature", "path")
            ),
            ImageItem(
                id = "5",
                url = "https://picsum.photos/800/600?random=5",
                title = "Mountain Peak",
                description = "Snowy mountain summit",
                width = 800,
                height = 600,
                thumbnail = "https://picsum.photos/400/300?random=5",
                tags = listOf("mountain", "snow", "peak")
            ),
            ImageItem(
                id = "6",
                url = "https://picsum.photos/800/600?random=6",
                title = "City Lights",
                description = "Night cityscape with lights",
                width = 800,
                height = 600,
                thumbnail = "https://picsum.photos/400/300?random=6",
                tags = listOf("city", "lights", "night")
            )
        )
    }
}

// Extension function untuk convert UnsplashPhoto ke ImageItem
private fun UnsplashPhoto.toImageItem(): ImageItem {
    return ImageItem(
        id = id,
        url = urls.regular,
        title = alt_description ?: description ?: "Untitled",
        description = description ?: alt_description ?: "No description available",
        width = width,
        height = height,
        thumbnail = urls.small,
        tags = tags?.map { it.title } ?: emptyList()
    )
}