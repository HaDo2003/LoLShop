package com.example.lolshop.model

import java.io.Serializable

data class Product(
    val id: String = "",
    val name: String = "",
    val categoryId: String = "",
    val price: String = "",
    val description: String = "",
    val showRecommended: Boolean = false,
    val imageUrl: String = "",
    var numberInCart: Int =0,
) : Serializable
