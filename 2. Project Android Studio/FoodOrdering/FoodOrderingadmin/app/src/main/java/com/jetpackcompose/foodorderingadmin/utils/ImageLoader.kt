package com.jetpackcompose.foodorderingadmin.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jetpackcompose.foodorderingadmin.R

object ImageLoader {
    
    fun loadImage(context: Context, url: String?, imageView: ImageView, placeholder: Int = R.drawable.ic_launcher_background) {
        val requestOptions = RequestOptions()
            .placeholder(placeholder)
            .error(placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
        
        Glide.with(context)
            .load(url)
            .apply(requestOptions)
            .into(imageView)
    }
    
    fun loadCircularImage(context: Context, url: String?, imageView: ImageView, placeholder: Int = R.drawable.ic_launcher_background) {
        val requestOptions = RequestOptions()
            .placeholder(placeholder)
            .error(placeholder)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
        
        Glide.with(context)
            .load(url)
            .apply(requestOptions)
            .into(imageView)
    }
}
