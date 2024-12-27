package com.example.lolshop.model

import com.google.firebase.Timestamp

data class Order(
    val customerId: String = "",
    val products: List<OrderProduct>,
    val TotalPriceOfAllProduct: Double = 0.0,
    val tax: Double = 0.0,
    val DeliveryFee: Double = 10.0,
    val TotalPriceAtAll: Double = 0.0,
    val orderDate: Timestamp = Timestamp.now(),
    val status: String = ""
)

data class OrderProduct(
    val productId: String = "",
    val quantity: Int = 0,
    val totalPriceOfProduct: Double = 0.0,
    val price: Double,
    val imageUrl: String = ""
)