package com.example.lolshop.repository

import android.content.Context
import com.example.lolshop.model.Order
import com.example.lolshop.model.OrderProduct
import com.google.firebase.firestore.FirebaseFirestore
import com.example.lolshop.utils.Result
import kotlinx.coroutines.tasks.await

class OrderRepository(
    private val firestore: FirebaseFirestore,
    private val context: Context
) {
    // Fetch User's Order
    suspend fun fetchUserOrders(uid: String): Result<List<Order>> {
        return try {
            // Fetch orders from Firestore
            val orderQuerySnapshot = firestore.collection("Orders")
                .whereEqualTo("customerId", uid)
                .get()
                .await()

            // Map documents to orders
            val orders = orderQuerySnapshot.documents.mapNotNull { document ->
                val order = document.toObject(Order::class.java)
                order?.apply {
                    // Extract and map products for each order
                    val productsRaw = document.get("products") as? List<Map<String, Any>> ?: emptyList()
                    val productList = productsRaw.map { productMap ->
                        val productId = productMap["productId"] as? String ?: "Unknown"
                        val name = productMap["name"] as? String ?: "Unnamed Product"
                        val price = (productMap["price"] as? Number)?.toDouble() ?: 0.0
                        val quantity = (productMap["quantity"] as? Number)?.toInt() ?: 0
                        val totalPriceOfProduct = (productMap["totalPriceOfProduct"] as? Number)?.toDouble() ?: 0.0
                        val imageUrl = productMap["imageUrl"] as? String ?: ""

                        OrderProduct(
                            productId = productId,
                            name = name,
                            quantity = quantity,
                            price = price,
                            totalPriceOfProduct = totalPriceOfProduct,
                            imageUrl = imageUrl
                        )
                    }

                    // Attach the mapped products to the order
                    this.products = productList
                }
            }

            // Return the result based on whether orders are found
            if (orders.isNotEmpty()) {
                Result.Success(orders)
            } else {
                Result.Empty
            }

        } catch (e: Exception) {
            // Catch and return errors
            Result.Error(e)
        }
    }

    // Fetch All Order
    suspend fun fetchAllOrders(): Result<List<Order>> {
        return try {
            // Fetch orders from Firestore
            val orderQuerySnapshot = firestore
                .collection("Orders")
                .get()
                .await()

            // Map documents to orders
            val orders = orderQuerySnapshot.documents.mapNotNull { document ->
                val order = document.toObject(Order::class.java)
                order?.apply {
                    // Extract and map products for each order
                    val productsRaw = document.get("products") as? List<Map<String, Any>> ?: emptyList()
                    val productList = productsRaw.map { productMap ->
                        val productId = productMap["productId"] as? String ?: "Unknown"
                        val name = productMap["name"] as? String ?: "Unnamed Product"
                        val price = (productMap["price"] as? Number)?.toDouble() ?: 0.0
                        val quantity = (productMap["quantity"] as? Number)?.toInt() ?: 0
                        val totalPriceOfProduct = (productMap["totalPriceOfProduct"] as? Number)?.toDouble() ?: 0.0
                        val imageUrl = productMap["imageUrl"] as? String ?: ""

                        OrderProduct(
                            productId = productId,
                            name = name,
                            quantity = quantity,
                            price = price,
                            totalPriceOfProduct = totalPriceOfProduct,
                            imageUrl = imageUrl
                        )
                    }

                    // Attach the mapped products to the order
                    this.products = productList
                }
            }

            // Return the result based on whether orders are found
            if (orders.isNotEmpty()) {
                Result.Success(orders)
            } else {
                Result.Empty
            }

        } catch (e: Exception) {
            // Catch and return errors
            Result.Error(e)
        }
    }

    suspend fun getCustomerName(uid: String): String{
        return try {
            val customerDoc = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid)
                .get()
                .await()

            customerDoc.getString("full_name") ?: "Unknown Customer"
        } catch (e: Exception) {
            "Unknown Customer"
        }
    }

    suspend fun confirm(orderId: String) {
        try {
            FirebaseFirestore.getInstance()
                .collection("Orders")
                .document(orderId)
                .update(mapOf(
                    "status" to "Delivering",
                ))
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to confirm order: ${e.message}")
        }
    }

    suspend fun complete(orderId: String) {
        try {
            FirebaseFirestore.getInstance()
                .collection("Orders")
                .document(orderId)
                .update(mapOf(
                    "status" to "Delivered",
                ))
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to complete order: ${e.message}")
        }
    }

    suspend fun cancel(orderId: String) {
        try {
            FirebaseFirestore.getInstance()
                .collection("Orders")
                .document(orderId)
                .update(mapOf(
                    "status" to "Cancelled",
                ))
                .await()
        } catch (e: Exception) {
            throw Exception("Failed to cancel order: ${e.message}")
        }
    }

    suspend fun filterStatus(status: String): Result<List<Order>> {
        return try {
            val query = firestore.collection("Orders")
            val querySnapshot = if (status == "All") {
                query.get().await() // Fetch all orders if "All" is selected
            } else {
                query.whereEqualTo("status", status).get().await() // Fetch orders by specific status
            }

            val filteredOrders = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Order::class.java)
            }

            if (filteredOrders.isNotEmpty()) {
                Result.Success(filteredOrders) // Return the filtered orders
            } else {
                Result.Empty // No orders match the filter
            }
        } catch (e: Exception) {
            Result.Error(e) // Return the error result
        }
    }
}