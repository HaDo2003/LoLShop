package com.example.lolshop.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.lolshop.model.Banner
import com.example.lolshop.utils.CloudinaryHelper
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BannerRepository(private val context: Context) {
    private val database = FirebaseDatabase.getInstance().reference.child("banner")
    private val cloudinaryHelper = CloudinaryHelper(context)

    // Fetch banners from Firebase
    suspend fun fetchBanner(): List<Banner> {
        return withContext(Dispatchers.IO) {
            val snapshot = database.get().await()
            snapshot.children.mapNotNull { it.getValue(Banner::class.java) }
        }
    }

    // Add banners to Firebase
    suspend fun addBanner(imageUri: Uri?) {
        imageUri?.let { uri ->
            val file = cloudinaryHelper.uriToFile(uri)
            if (file != null) {
                try {
                    // Upload image to Cloudinary
                    val downloadUrl = cloudinaryHelper.uploadImageToCloudinary(file, "MobileProject/BannerImages")

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
}