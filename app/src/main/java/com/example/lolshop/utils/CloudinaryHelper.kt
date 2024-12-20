package com.example.lolshop.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class CloudinaryHelper(private val context: Context) {
    // Convert URI to File
    fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
            inputStream?.copyTo(tempFile.outputStream())
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    // Upload image to Cloudinary
    suspend fun uploadImageToCloudinary(file: File, Directory: String): String {
        return withContext(Dispatchers.IO) {
            val requestParams = mapOf(
                "public_id" to UUID.randomUUID().toString(),
                "overwrite" to true,
                "folder" to Directory
            )
            try {
                val result = CloudinaryConfig.cloudinary.uploader().upload(file, requestParams)

                // Ensure the URL is HTTPS
                val imageUrl = result["url"]?.toString()

                // If URL is HTTP, you can manually replace it with HTTPS
                if (imageUrl != null && imageUrl.startsWith("http://")) {
                    return@withContext imageUrl.replace("http://", "https://")
                }

                // Return the URL (it should already be HTTPS)
                return@withContext imageUrl ?: throw Exception("Image upload failed")
            } catch (e: Exception) {
                throw Exception("Image upload failed: ${e.message}")
            }
        }
    }
}