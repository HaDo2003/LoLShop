package com.example.lolshop.model

import java.io.Serializable

data class Product(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val description: String = "",
    val categoryId: String = "",
    val showRecommended: Boolean = false,
    val imageUrl: String = ""
) : Serializable
