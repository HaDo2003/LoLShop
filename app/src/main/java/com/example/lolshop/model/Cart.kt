package com.example.lolshop.model

data class Cart(
    val cartId: String,
    val products: List<CartProduct> = emptyList(),
    val total: Double
)

data class CartProduct(
    val productId: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String = ""
)