package com.example.lolshop.repository

import android.content.Context
import android.util.Log
import com.example.lolshop.model.Cart
import com.example.lolshop.model.CartProduct
import com.example.lolshop.model.Product
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.lolshop.utils.Result

class CartRepository(
    private val firestore: FirebaseFirestore,
    private val realtimeDatabase: FirebaseDatabase,
    private val context: Context
) {
    suspend fun addProductToCart(uid: String, productId: String) {
        try {
            // Fetch product details from Realtime Database
            val productRef = realtimeDatabase.getReference("products").child(productId)
            val snapshot = productRef.get().await()

            if (snapshot.exists()) {
                val product = snapshot.getValue(Product::class.java)

                if (product != null) {
                    val cartRef = firestore.collection("Carts").document(uid)

                    // Get current cart
                    val cartDoc = cartRef.get().await()
                    if (cartDoc.exists()) {
                        // Safely cast the products field to List<HashMap<String, Any>>
                        @Suppress("UNCHECKED_CAST")
                        val products = cartDoc.get("products") as? List<HashMap<String, Any>> ?: listOf()

                        // Check if the product already exists in the cart
                        val existingProductIndex = products.indexOfFirst {
                            it["productId"] == productId
                        }

                        val updatedProducts = products.toMutableList()
                        var newTotalPrice = cartDoc.getDouble("total") ?: 0.0

                        if (existingProductIndex != -1) {
                            // Update quantity if the product exists
                            val currentProduct = updatedProducts[existingProductIndex]
                            val currentQuantity = (currentProduct["quantity"] as? Long ?: 0L)
                            val currentPrice = (currentProduct["price"] as? Double ?: 0.0)

                            updatedProducts[existingProductIndex] = hashMapOf(
                                "productId" to productId,
                                "quantity" to (currentQuantity + 1),
                                "price" to (currentPrice + product.price.toDouble()),
                                "name" to product.name,
                                "imageUrl" to product.imageUrl
                            )
                            // Update the total price
                            newTotalPrice += product.price.toDouble()
                        } else {
                            // Add new product if it doesn't exist
                            updatedProducts.add(
                                hashMapOf(
                                    "productId" to productId,
                                    "quantity" to 1L,
                                    "price" to product.price,
                                    "name" to product.name,
                                    "imageUrl" to product.imageUrl
                                )
                            )
                            // Update the total price
                            newTotalPrice += product.price.toDouble()
                        }

                        // Update Firestore with the new list of products
                        firestore.collection("Carts")
                            .document(uid)
                            .update(
                                "products", updatedProducts,
                                "total", newTotalPrice
                            )
                            .await()
                    } else {
                        // If the cart doesn't exist, create a new cart for the user
                        val newCart = hashMapOf(
                            "products" to listOf(
                                hashMapOf(
                                    "productId" to productId,
                                    "quantity" to 1L,
                                    "price" to product.price,
                                    "name" to product.name,
                                    "imageUrl" to product.imageUrl
                                )
                            ),
                            "totalPrice" to product.price.toDouble()
                        )
                        cartRef.set(newCart).await()
                    }
                }
            } else {
                Log.e("CartRepository", "Product not found in Realtime Database")
            }
        } catch (e: Exception) {
            // Handle the error (log, display a message, etc.)
            Log.e("CartRepository", "Error adding product to cart", e)
            throw e // Re-throw the exception to handle it in the ViewModel/UI
        }
    }

    // Fetch cart data for a specific user
    suspend fun getCart(uid: String): Result<Cart> {
        return try {
            val cartDoc = firestore.collection("Carts")
                .document(uid)
                .get()
                .await()

            if (!cartDoc.exists()) return Result.Empty

            val snapshot = firestore.collection("Carts")
                .document(uid)
                .collection("products")
                .get()
                .await()

            val products = snapshot.documents.mapNotNull { document ->
                document.toObject(CartProduct::class.java)
            }
            val total = cartDoc.getDouble("total") ?: 0.0

            Result.Success(Cart(
                cartId = uid,
                products = products,
                total = total
            ))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Function to update cart item quantity
    suspend fun updateProductQuantity(uid: String, productId: String, newQuantity: Int): Result<Unit> {
        return try {
            val productRef = firestore.collection("Carts")
                .document(uid)
                .collection("products")
                .document(productId)

            val productDoc = productRef.get().await()

            if (!productDoc.exists()) return Result.Empty

            productRef.update("quantity", newQuantity).await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}