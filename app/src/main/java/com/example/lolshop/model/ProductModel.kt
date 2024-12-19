package com.example.lolshop.model

import java.io.Serializable
import java.util.ArrayList

data class ProductModel(
    var name: String = "",
    var description: String = "",
    var imageUrl: ArrayList<String> = ArrayList(),
    var model: ArrayList<String> = ArrayList(),
    var price: Double=0.0,
    var numberInCart: Int=0,
    var showRecommended: Boolean = false,
    var categoryId: String=""

) : Serializable
