package com.example.lolshop.view.admin

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
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
import com.example.lolshop.viewmodel.homepage.OrderViewModel
import com.example.lolshop.viewmodel.homepage.OrderViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.properties.Delegates


class AdminActivity : BaseActivity() {
    private lateinit var uid: String
    private var isAdmin by Delegates.notNull<Boolean>()
    private val adminViewModel: AdminViewModel by viewModels {
        AdminViewModelFactory(applicationContext)
    }
    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModelFactory(applicationContext)
    }
    private val bannerViewModel: BannerViewModel by viewModels {
        BannerViewModelFactory(applicationContext)
    }
    private val orderViewModel: OrderViewModel by viewModels {
        OrderViewModelFactory(
            FirebaseFirestore.getInstance()
            ,applicationContext
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uid = intent.getStringExtra("uid") ?: ""
        isAdmin = intent.getBooleanExtra("isAdmin", false)
        Log.d("uid", uid)
        Log.d("isAdmin", isAdmin.toString())
        val productRepository = ProductRepository(applicationContext)
        val categoryRepository = CategoryRepository(applicationContext)
        val bannerRepository = BannerRepository(applicationContext)
        setContent {
            AdminScreen(
                uid,
                isAdmin,
                adminViewModel = adminViewModel,
                categoryViewModel = categoryViewModel,
                bannerViewModel = bannerViewModel,
                productRepository = productRepository,
                categoryRepository = categoryRepository,
                bannerRepository = bannerRepository,
                orderViewModel = orderViewModel
            )
        }
    }
}
