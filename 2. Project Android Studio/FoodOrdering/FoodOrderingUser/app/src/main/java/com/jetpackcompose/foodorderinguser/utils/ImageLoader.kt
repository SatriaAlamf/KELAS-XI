package com.jetpackcompose.foodorderinguser.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jetpackcompose.foodorderinguser.R

object ImageLoader {
    
    fun loadImage(context: Context, imageView: ImageView, imageUrl: String?) {
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
        
        Glide.with(context)
            .load(imageUrl ?: Constants.DEFAULT_FOOD_IMAGE)
            .apply(requestOptions)
            .into(imageView)
    }
    
    fun loadCircularImage(context: Context, imageView: ImageView, imageUrl: String?) {
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
        
        Glide.with(context)
            .load(imageUrl ?: Constants.DEFAULT_USER_IMAGE)
            .apply(requestOptions)
            .into(imageView)
    }
    
    fun loadCategoryImage(context: Context, imageView: ImageView, imageUrl: String?) {
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
        
        Glide.with(context)
            .load(imageUrl ?: Constants.DEFAULT_CATEGORY_IMAGE)
            .apply(requestOptions)
            .into(imageView)
    }
    
    fun loadRestaurantImage(context: Context, imageView: ImageView, imageUrl: String?) {
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
        
        Glide.with(context)
            .load(imageUrl ?: Constants.DEFAULT_RESTAURANT_IMAGE)
            .apply(requestOptions)
            .into(imageView)
    }
}