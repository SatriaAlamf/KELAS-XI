package com.example.whatsapp.utils

import android.content.Context
import android.net.Uri
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileUploadHelper @Inject constructor(
    private val storage: Storage
) {
    
    suspend fun uploadImage(
        context: Context,
        uri: Uri,
        bucket: String,
        folder: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fileName = "${folder}${UUID.randomUUID()}.jpg"
            val file = uriToFile(context, uri)
            
            if (file.length() > Constants.MAX_IMAGE_SIZE) {
                return@withContext Result.failure(Exception("Image file too large"))
            }
            
            val bytes = FileInputStream(file).readBytes()
            
            val result = storage.from(bucket).upload(fileName, bytes)
            val publicUrl = storage.from(bucket).publicUrl(fileName)
            
            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun uploadVideo(
        context: Context,
        uri: Uri,
        bucket: String,
        folder: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fileName = "${folder}${UUID.randomUUID()}.mp4"
            val file = uriToFile(context, uri)
            
            if (file.length() > Constants.MAX_VIDEO_SIZE) {
                return@withContext Result.failure(Exception("Video file too large"))
            }
            
            val bytes = FileInputStream(file).readBytes()
            
            val result = storage.from(bucket).upload(fileName, bytes)
            val publicUrl = storage.from(bucket).publicUrl(fileName)
            
            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun uploadAudio(
        context: Context,
        file: File,
        bucket: String,
        folder: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fileName = "${folder}${UUID.randomUUID()}.m4a"
            
            val bytes = FileInputStream(file).readBytes()
            
            val result = storage.from(bucket).upload(fileName, bytes)
            val publicUrl = storage.from(bucket).publicUrl(fileName)
            
            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun uploadDocument(
        context: Context,
        uri: Uri,
        bucket: String,
        folder: String = "",
        originalName: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val extension = originalName.substringAfterLast(".", "")
            val fileName = "${folder}${UUID.randomUUID()}.$extension"
            val file = uriToFile(context, uri)
            
            if (file.length() > Constants.MAX_DOCUMENT_SIZE) {
                return@withContext Result.failure(Exception("Document file too large"))
            }
            
            val bytes = FileInputStream(file).readBytes()
            
            val result = storage.from(bucket).upload(fileName, bytes)
            val publicUrl = storage.from(bucket).publicUrl(fileName)
            
            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp", null, context.cacheDir)
        tempFile.outputStream().use { output ->
            inputStream?.copyTo(output)
        }
        return tempFile
    }
    
    suspend fun deleteFile(bucket: String, fileName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            storage.from(bucket).delete(fileName)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}