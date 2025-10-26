package com.example.onlineimageapp.data.network

import com.example.onlineimageapp.data.model.UnsplashPhoto
import com.example.onlineimageapp.data.model.UnsplashResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashApiService {
    
    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("orientation") orientation: String? = null,
        @Query("client_id") clientId: String = API_KEY
    ): Response<UnsplashResponse>
    
    @GET("photos")
    suspend fun getPhotos(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("order_by") orderBy: String = "latest",
        @Query("client_id") clientId: String = API_KEY
    ): Response<List<UnsplashPhoto>>
    
    @GET("photos/{id}")
    suspend fun getPhoto(
        @Path("id") id: String,
        @Query("client_id") clientId: String = API_KEY
    ): Response<UnsplashPhoto>
    
    companion object {
        const val BASE_URL = "https://api.unsplash.com/"
        // Demo API Key - untuk production silakan daftar di unsplash.com/developers
        const val API_KEY = "demo_key" 
    }
}