package com.example.lolshop.view.admin

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.lolshop.repository.BannerRepository
import com.example.lolshop.repository.CategoryRepository
import com.example.lolshop.repository.ProductRepository
import com.example.lolshop.view.BaseActivity
import com.example.lolshop.viewmodel.admin.AdminViewModel
import com.example.lolshop.viewmodel.admin.AdminViewModelFactory
import com.example.lolshop.viewmodel.admin.BannerViewModel
import com.example.lolshop.viewmodel.admin.BannerViewModelFactory
import com.example.lolshop.viewmodel.admin.CategoryViewModel
import com.example.lolshop.viewmodel.admin.CategoryViewModelFactory


class AdminActivity : BaseActivity() {
    private val adminViewModel: AdminViewModel by viewModels {
        AdminViewModelFactory(applicationContext)
    }
    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModelFactory(applicationContext)
    }
    private val bannerViewModel: BannerViewModel by viewModels {
        BannerViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val productRepository = ProductRepository(applicationContext)
        val categoryRepository = CategoryRepository(applicationContext)
        val bannerRepository = BannerRepository(applicationContext)
        setContent {
            AdminScreen(
                adminViewModel = adminViewModel,
                categoryViewModel = categoryViewModel,
                bannerViewModel = bannerViewModel,
                productRepository = productRepository,
                categoryRepository = categoryRepository,
                bannerRepository = bannerRepository
            )
        }
    }

}
