package com.example.lolshop.model

import com.google.type.Date

data class Order(
    val id: String = "",
    val products: List<OrderProduct>,
    val TotalPriceOfAllProduct: Double = 0.0,
    val DeliveryFee: Double = 10.0,
    val TotalPriceAtAll: Double = 0.0,
    val orderDate: Date,
    val status: String = ""
)

data class OrderProduct(
    val orderId: String = "",
    val productId: String = "",
    val quantity: Int = 0,
    val totalPriceOfProduct: Double = 0.0
)