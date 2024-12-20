package com.example.lolshop.view.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.lolshop.repository.ProductRepository
import com.example.lolshop.viewmodel.AdminViewModel
import com.example.lolshop.viewmodel.AdminViewModelFactory


class AdminActivity : ComponentActivity() {
    private val adminViewModel: AdminViewModel by viewModels {
        AdminViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val productRepository = ProductRepository(applicationContext)

        setContent {
            AdminScreen(
                adminViewModel = adminViewModel,
                productRepository = productRepository
            )
        }
    }

}
