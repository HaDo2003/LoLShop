package com.example.lolshop.view.admin

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lolshop.model.Product
import com.example.lolshop.repository.ProductRepository
import com.example.lolshop.viewmodel.admin.AdminViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(
    adminViewModel: AdminViewModel,
    productRepository: ProductRepository
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

//    // Background screen (homepage)
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Gray.copy(alpha = 0.5f))
//    ) {
//        // Left admin panel
//        Surface(
//            modifier = Modifier
//                .fillMaxHeight()
//                .fillMaxWidth(0.8f) // Take 4/5 of the width
//                .align(Alignment.CenterStart),
//            color = Color.White,
//            shadowElevation = 4.dp
//        ) {
            NavHost(navController = navController, startDestination = "admin_main") {
                composable("admin_main") {
                    AdminMainScreen(navController = navController)
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
                    AddBannerScreen(adminViewModel = adminViewModel, navController = navController)
                }
                composable("add_category") {
                    AddCategoryScreen(adminViewModel = adminViewModel, navController = navController)
                }
                composable("manage_product") {
                    ManageProductScreen(adminViewModel = adminViewModel, navController = navController)
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
                    ManageOrderScreen()
                }
            }
        }
//    }
//}


@Composable
fun AdminMainScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome Admin")

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("add_product") }) {
            Text("Add Product")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("add_banner") }) {
            Text("Add Banner")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("add_category") }) {
            Text("Add Category")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("manage_product") }) {
            Text("Manage Products")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("manage_order") }) {
            Text("Manage Orders")
        }
    }
}
