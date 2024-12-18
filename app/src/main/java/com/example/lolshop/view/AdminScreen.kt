package com.example.lolshop.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lolshop.model.Product
import com.example.lolshop.repository.ProductRepository
import com.example.lolshop.viewmodel.AdminViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(adminViewModel: AdminViewModel, productRepository: ProductRepository) {
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
        composable("manage_product") {
            ManageProductScreen(adminViewModel = adminViewModel, navController = navController)
        }
        composable("manage_order") {
            ManageOrderScreen()
        }
    }
}


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
        Button(onClick = { navController.navigate("add_product") }) {
            Text("Add Product")
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
