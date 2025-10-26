package com.example.onlineimageapp.data.model

data class ImageItem(
    val id: String,
    val url: String,
    val title: String,
    val description: String,
    val width: Int = 0,
    val height: Int = 0,
    val thumbnail: String = url,
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false
)

data class ImageResponse(
    val images: List<ImageItem>,
    val page: Int = 1,
    val perPage: Int = 20,
    val total: Int = 0,
    val totalPages: Int = 0
)

data class UnsplashPhoto(
    val id: String,
    val urls: UnsplashUrls,
    val description: String?,
    val alt_description: String?,
    val width: Int,
    val height: Int,
    val user: UnsplashUser,
    val tags: List<UnsplashTag>? = null
)

data class UnsplashUrls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String
)

data class UnsplashUser(
    val id: String,
    val username: String,
    val name: String
)

data class UnsplashTag(
    val title: String
)

data class UnsplashResponse(
    val results: List<UnsplashPhoto>,
    val total: Int,
    val total_pages: Int
)