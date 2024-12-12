package com.example.lolshop.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.lolshop.model.Product
import com.example.lolshop.utils.CloudinaryConfig
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class ProductRepository(private val context: Context) {

    private val database = FirebaseDatabase.getInstance().reference.child("products")

    // Fetch products from Firebase
    suspend fun fetchProducts(): List<Product> {
        return withContext(Dispatchers.IO) {
            val snapshot = database.get().await()
            snapshot.children.mapNotNull { it.getValue(Product::class.java) }
        }
    }

    // Add product to Firebase
    suspend fun addProduct(name: String, price: String, description: String, category: String, imageUri: Uri?) {
        imageUri?.let { uri ->
            val file = uriToFile(uri)
            if (file != null) {
                try {
                    // Upload image to Cloudinary
                    val downloadUrl = uploadImageToCloudinary(file)

                    // Save product to Firebase
                    val productId = database.push().key ?: return@let
                    val product = Product(productId, name, price, description, category, downloadUrl)

                    database.child(productId).setValue(product)
                } catch (e: Exception) {
                    Log.e("ProductRepository", "Error uploading image or adding product: ${e.message}")
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
                "folder" to "MobileProject"
            )
            val result = CloudinaryConfig.cloudinary.uploader().upload(file, requestParams)
            result["url"]?.toString() ?: throw Exception("Image upload failed")
        }
    }
}
