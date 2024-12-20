package com.example.lolshop.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.lolshop.model.Category
import com.example.lolshop.utils.CloudinaryConfig
import com.example.lolshop.utils.CloudinaryHelper
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class CategoryRepository(private val context: Context) {
    private val database = FirebaseDatabase.getInstance().reference.child("category")
    private val cloudinaryHelper = CloudinaryHelper(context)

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
            val file = cloudinaryHelper.uriToFile(uri)
            if (file != null) {
                try {
                    // Upload image to Cloudinary
                    val downloadUrl = cloudinaryHelper.uploadImageToCloudinary(file, "MobileProject/CategoryImages")

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
}