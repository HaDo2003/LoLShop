package com.example.lolshop.view.admin

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lolshop.model.Product
import com.example.lolshop.repository.BannerRepository
import com.example.lolshop.repository.CategoryRepository
import com.example.lolshop.repository.ProductRepository
import com.example.lolshop.view.homepage.BottomMenu
import com.example.lolshop.view.homepage.CartActivity
import com.example.lolshop.view.homepage.MainScreen
import com.example.lolshop.view.homepage.OrderActivity
import com.example.lolshop.view.homepage.UserProfile
import com.example.lolshop.viewmodel.admin.AdminViewModel
import com.example.lolshop.viewmodel.admin.BannerViewModel
import com.example.lolshop.viewmodel.admin.CategoryViewModel
import com.example.lolshop.viewmodel.homepage.OrderViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(
    uid: String,
    isAdmin: Boolean,
    adminViewModel: AdminViewModel,
    categoryViewModel: CategoryViewModel,
    bannerViewModel: BannerViewModel,
    productRepository: ProductRepository,
    categoryRepository: CategoryRepository,
    bannerRepository: BannerRepository,
    orderViewModel: OrderViewModel
) {
    val navController = rememberNavController()

    val imageUriState = remember { mutableStateOf<Uri?>(null) }
    val productsList = remember { mutableStateOf(emptyList<Product>()) }
    val totalProducts = productsList.value.size
    val fetchProducts: () -> Unit = {
        CoroutineScope(Dispatchers.IO).launch {
            val products = productRepository.fetchProducts()
            productsList.value = products
        }
    }
    val imageResultLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUriState.value = uri
    }

    NavHost(navController = navController, startDestination = "admin_main") {
        composable("admin_main") {
            val context = LocalContext.current
            AdminMainScreen(
                navController = navController,
                onCartClick = {
                    val intent = Intent(context, CartActivity::class.java).apply {
                        putExtra("uid", uid)
                        putExtra("isAdmin", isAdmin)
                    }
                    context.startActivity(intent)
                },
                onProfileClick = {
                    val intent = Intent(context, UserProfile::class.java).apply {
                        putExtra("uid", uid)
                        putExtra("isAdmin", isAdmin)
                    }
                    context.startActivity(intent)
                },
                onAdminClick = {
                    val intent = Intent(context, AdminActivity::class.java).apply {
                        putExtra("uid", uid)
                        putExtra("isAdmin", isAdmin)
                    }
                    context.startActivity(intent)
                },
                onHomeClick = {
                    val intent = Intent(context, MainScreen::class.java).apply {
                        putExtra("uid", uid)
                        putExtra("isAdmin", isAdmin)
                    }
                    context.startActivity(intent)
                },
                onOrderClick = {
                    val intent = Intent(context, OrderActivity::class.java).apply {
                        putExtra("uid", uid)
                        putExtra("isAdmin", isAdmin)
                    }
                    context.startActivity(intent)
                },
            )
        }
        composable("add_product") {
            AddProductScreen(
                adminViewModel = adminViewModel,
                navController = navController,
                productRepository = productRepository,
                imageUriState = imageUriState.value,
                productsList = productsList.value,
                totalProducts = totalProducts,
                imageResultLauncher = imageResultLauncher,
                fetchProducts = fetchProducts
            )
        }
        composable("add_banner") {
            AddBannerScreen(
                bannerViewModel = bannerViewModel,
                bannerRepository = bannerRepository,
                navController = navController)
        }
        composable("add_category") {
            AddCategoryScreen(
                categoryViewModel = categoryViewModel,
                categoryRepository = categoryRepository,
                navController = navController)
        }
        composable("manage_product") {
            ManageProductScreen(
                adminViewModel = adminViewModel,
                navController = navController
            )
        }
        composable("edit_product/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            if (productId != null) {
                EditProductScreen(productId = productId, adminViewModel = adminViewModel, navController = navController)
            } else {
                Log.e("NavHost", "Product ID is null in edit_product route")
            }
        }
        composable("manage_order") {
            ManageOrderScreen(
                orderViewModel = orderViewModel,
                navController = navController
            )
        }
    }
}


@Composable
fun AdminMainScreen(
    navController: NavController,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAdminClick:() -> Unit,
    onHomeClick:() -> Unit,
    onOrderClick:() -> Unit
) {
    val currentScreen = "admin"
    val scrollState = rememberScrollState()
    Scaffold(
        bottomBar = {
            BottomMenu(
                isAdmin = true,
                modifier = Modifier
                    .fillMaxWidth(),
                onItemClick = onCartClick,
                onProfileClick = onProfileClick,
                onAdminClick = onAdminClick,
                onHomeClick = onHomeClick,
                onOrderClick = onOrderClick,
                currentScreen = currentScreen
            )
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 0.dp, // Override any top padding caused by Scaffold
                    bottom = paddingValue.calculateBottomPadding(),
                )
                .background(Color.White)
                .verticalScroll(scrollState)
                .padding(bottom = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome Admin",
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.size(width = 300.dp, height = 40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                onClick = { navController.navigate("add_product") }
            ) {
                Text("Add Product")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.size(width = 300.dp, height = 40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                onClick = { navController.navigate("add_banner") }
            ) {
                Text("Add Banner")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.size(width = 300.dp, height = 40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                onClick = { navController.navigate("add_category") }
            ) {
                Text("Add Category")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.size(width = 300.dp, height = 40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                onClick = { navController.navigate("manage_product") }
            ) {
                Text("Manage Products")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.size(width = 300.dp, height = 40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                onClick = { navController.navigate("manage_order") }
            ) {
                Text("Manage Orders")
            }
        }
    }
}
