package com.example.lolshop.model

import com.google.firebase.Timestamp

data class Order(
    val orderId: String = "",
    val customerId: String = "",
    var products: List<OrderProduct> = emptyList(),
    val totalPriceOfAllProduct: Double = 0.0,
    val tax: Double = 0.0,
    val deliveryFee: Double = 10.0,
    val totalPriceAtAll: Double = 0.0,
    val orderDate: Timestamp = Timestamp.now(),
    val status: String = ""
)

data class OrderProduct(
    val productId: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val totalPriceOfProduct: Double = 0.0,
    val imageUrl: String = ""
)