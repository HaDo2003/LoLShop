package com.example.lolshop.view.admin

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.lolshop.repository.ProductRepository
import com.example.lolshop.view.BaseActivity
import com.example.lolshop.viewmodel.admin.AdminViewModel
import com.example.lolshop.viewmodel.admin.AdminViewModelFactory


class AdminActivity : BaseActivity() {
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
