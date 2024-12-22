package com.example.lolshop.model

import com.google.firebase.database.PropertyName
import java.io.Serializable

data class User(
    val id: String = "",
    val full_name: String = "",
    val email: String = "",
    val phone_number: String = "",
    val address: String = "",
    @get:PropertyName("isAdmin") val isAdmin: Boolean = false
) : Serializable
