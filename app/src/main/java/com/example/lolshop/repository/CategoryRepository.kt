package com.example.lolshop.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.lolshop.model.Category
import com.example.lolshop.utils.CloudinaryConfig
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class CategoryRepository(private val context: Context) {
    private val database = FirebaseDatabase.getInstance().reference.child("category")

    // Fetch categories from Firebase
    suspend fun fetchCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            val snapshot = database.get().await()
            snapshot.children.mapNotNull { it.getValue(Category::class.java) }
        }
    }

    // Add category to Firebase
    suspend fun addCategory(name: String, imageUri: Uri?) {
        imageUri?.let { uri ->
            val file = uriToFile(uri)
            if (file != null) {
                try {
                    // Upload image to Cloudinary
                    val downloadUrl = uploadImageToCloudinary(file)

                    // Save category to Firebase
                    val categoryId = database.push().key ?: return@let
                    val category = Category(categoryId, name, downloadUrl)

                    database.child(categoryId).setValue(category)
                } catch (e: Exception) {
                    Log.e("CategoryRepository", "Error uploading image or adding category: ${e.message}")
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
                "folder" to "MobileProject/CategoryImages"
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