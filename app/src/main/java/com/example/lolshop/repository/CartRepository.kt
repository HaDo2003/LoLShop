package com.example.lolshop.repository

import android.content.Context
import android.util.Log
import com.example.lolshop.model.Cart
import com.example.lolshop.model.CartProduct
import com.example.lolshop.model.Order
import com.example.lolshop.model.OrderProduct
import com.example.lolshop.model.Product
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.lolshop.utils.Result
import com.google.firebase.Timestamp

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
                                    "price" to product.price.toDouble(),
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
                                    "price" to product.price.toDouble(),
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

            // Debug logs to check Firestore data
            val productsRaw = cartDoc.get("products")

            val products = productsRaw as? List<Map<String, Any>> ?: emptyList()
            // Map the products into the CartProduct model
            val productList = products.map { productMap ->
                val productId = productMap["productId"] as? String ?: "Unknown"
                val name = productMap["name"] as? String ?: "Unnamed Product"
                val price = (productMap["price"] as? Number)?.toDouble() ?: 0.0
                val quantity = (productMap["quantity"] as? Number)?.toInt() ?: 0
                val imageUrl = productMap["imageUrl"] as? String ?: ""

                CartProduct(
                    productId = productId,
                    name = name,
                    price = price,
                    quantity = quantity,
                    imageUrl = imageUrl
                )
            }
            val total = cartDoc.getDouble("total") ?: 0.0
            Log.d("Firestore", "Final mapped products: $productList")
            Result.Success(
                Cart(
                    cartId = uid,
                    products = productList,
                    total = total
                ) ,
            )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    // Function to update cart item quantity
    suspend fun updateProductQuantity(uid: String, productId: String, newQuantity: Int): Result<Unit> {
        return try {
            val cartRef = firestore.collection("Carts").document(uid)

            // Get the cart document
            val cartDoc = cartRef.get().await()

            if (!cartDoc.exists()) return Result.Empty

            // Get the list of products from the cart document
            val productsRaw = cartDoc.get("products") as? List<Map<String, Any>> ?: emptyList()

            // Find the product to update
            val updatedProducts = if (newQuantity == 0) {
                // If newQuantity is 0, remove the product
                productsRaw.filterNot { product ->
                    val productIdInCart = product["productId"] as? String ?: ""
                    productIdInCart == productId
                }
            } else {
                // Otherwise, update the quantity
                productsRaw.map { product ->
                    val productIdInCart = product["productId"] as? String ?: ""
                    if (productIdInCart == productId) {
                        // If the product matches, update the quantity
                        product.toMutableMap().apply {
                            this["quantity"] = newQuantity
                        }
                    } else {
                        product
                    }
                }
            }

            // Update the products field with the updated list
            cartRef.update("products", updatedProducts).await()

            // Optionally update the total price of the cart if needed
            val updatedTotal = updatedProducts.sumOf { product ->
                val price = (product["price"] as? Number)?.toDouble() ?: 0.0
                val quantity = (product["quantity"] as? Number)?.toInt() ?: 0
                price * quantity
            }

            // Update the total in the cart
            cartRef.update("total", updatedTotal).await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    //Function to Place order from cart
    suspend fun placeOrderFromCart(
        cart: Cart,
        uid: String,
        totalPriceOfAllProduct: Double,
        tax: Double,
        deliveryFee: Double,
        totalPriceAtAll: Double
    ) {
        try {
            // Map cart products to order products
            val orderProducts = cart.products.map { cartProduct ->
                OrderProduct(
                    productId = cartProduct.productId,
                    quantity = cartProduct.quantity,
                    totalPriceOfProduct = cartProduct.quantity * cartProduct.price,
                    price = cartProduct.price,
                    imageUrl = cartProduct.imageUrl
                )
            }

            // Create an Order object
            val order = Order(
                customerId = uid,
                products = orderProducts,
                TotalPriceOfAllProduct = totalPriceOfAllProduct,
                tax = tax,
                DeliveryFee = deliveryFee,
                TotalPriceAtAll = totalPriceAtAll,
                orderDate = Timestamp.now(),
                status = "Wait for confirmation"
            )

            // Save the order to Firestore
            val orderRef = firestore.collection("Orders").document()
            orderRef.set(order).await()

            // Clear the cart
            firestore.collection("Carts").document(uid).update("products", emptyList<CartProduct>(), "total", 0.0).await()

            Log.d("PlaceOrder", "Order placed successfully with ID: ${orderRef.id}")
        } catch (e: Exception) {
            Log.e("PlaceOrder", "Error placing order: ${e.localizedMessage}")
        }
    }
}