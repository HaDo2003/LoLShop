package com.example.lolshop.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.lolshop.model.Product
import com.example.lolshop.utils.CloudinaryConfig
import com.example.lolshop.utils.CloudinaryHelper
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class ProductRepository(private val context: Context) {
    private val database = FirebaseDatabase.getInstance().reference.child("products")
    private val cloudinaryHelper = CloudinaryHelper(context)

    // Fetch products from Firebase
    suspend fun fetchProducts(): List<Product> {
        return withContext(Dispatchers.IO) {
            val snapshot = database.get().await()
            snapshot.children.mapNotNull { it.getValue(Product::class.java) }
        }
    }

    // Add product to Firebase
    suspend fun addProduct(name: String, categoryId: String, price: String, description: String, showRecommended: Boolean, imageUri: Uri?) {
        imageUri?.let { uri ->
            val file = cloudinaryHelper.uriToFile(uri)
            if (file != null) {
                try {
                    // Upload image to Cloudinary
                    val downloadUrl = cloudinaryHelper.uploadImageToCloudinary(file, "MobileProject/ProductImages")

                    // Save product to Firebase
                    val productId = database.push().key ?: return@let
                    val product = Product(productId, name, categoryId, price, description, showRecommended, downloadUrl)

                    database.child(productId).setValue(product)
                } catch (e: Exception) {
                    Log.e("ProductRepository", "Error uploading image or adding product: ${e.message}")
                }
            }
        }
    }

    // Delete product from Realtime Database
    suspend fun deleteProduct(productId: String) {
        withContext(Dispatchers.IO) {
            try {
                database.child(productId).removeValue().await()
                Log.d("DeleteProduct", "Product deleted successfully")
            } catch (e: Exception) {
                Log.e("DeleteProduct", "Error deleting product", e)
            }
        }
    }

    fun updateProduct(productId: String, name: String, categoryId: String, price: String, description: String, showRecommended: Boolean, imageUrl: String) {
        val product = Product(productId, name, categoryId, price, description, showRecommended, imageUrl)
        database.child(productId).setValue(product)
    }

}
