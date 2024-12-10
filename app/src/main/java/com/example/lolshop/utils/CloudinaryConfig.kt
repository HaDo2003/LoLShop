package com.example.lolshop.utils

import com.cloudinary.Cloudinary

object CloudinaryConfig {
    val cloudinary: Cloudinary by lazy {
        Cloudinary(
            mapOf(
                "cloud_name" to "dwbibirzk",
                "api_key" to "468538554176855",
                "api_secret" to "ABQ9d6UC6vMZyqU3CUwPMA2eOX4"
            )
        )
    }
}
