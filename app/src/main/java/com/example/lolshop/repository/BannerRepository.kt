package com.example.lolshop.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.lolshop.model.Banner
import com.example.lolshop.utils.CloudinaryConfig
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class BannerRepository(private val context: Context) {
    private val database = FirebaseDatabase.getInstance().reference.child("banner")

    // Fetch banners from Firebase
    suspend fun fetchCategories(): List<Banner> {
        return withContext(Dispatchers.IO) {
            val snapshot = database.get().await()
            snapshot.children.mapNotNull { it.getValue(Banner::class.java) }
        }
    }

    // Add banners to Firebase
    suspend fun addBanner(imageUri: Uri?) {
        imageUri?.let { uri ->
            val file = uriToFile(uri)
            if (file != null) {
                try {
                    // Upload image to Cloudinary
                    val downloadUrl = uploadImageToCloudinary(file)

                    // Save category to Firebase
                    val bannerId = database.push().key ?: return@let
                    val banner = Banner(bannerId, downloadUrl)

                    database.child(bannerId).setValue(banner)
                } catch (e: Exception) {
                    Log.e("BannerRepository", "Error uploading image or adding category: ${e.message}")
                }
            }
        }
    }

    // Convert URI to File
    private fun uriToFile(uri: Uri): File? {
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
    private suspend fun uploadImageToCloudinary(file: File): String {
        return withContext(Dispatchers.IO) {
            val requestParams = mapOf(
                "public_id" to UUID.randomUUID().toString(),
                "overwrite" to true,
                "folder" to "MobileProject/BannerImages"
            )
            val result = CloudinaryConfig.cloudinary.uploader().upload(file, requestParams)
            result["url"]?.toString() ?: throw Exception("Image upload failed")
        }
    }
}