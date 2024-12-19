package com.example.lolshop.model

import java.io.Serializable

data class User(
    val id: String = "",
    val full_name: String = "",
    val phone_number: String = "",
    val address: String = "",
    val isAdmin: Boolean = false,
) : Serializable
