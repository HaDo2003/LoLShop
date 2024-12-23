package com.example.lolshop.view.admin

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lolshop.model.Screen
import com.example.lolshop.repository.ProductRepository
import com.example.lolshop.viewmodel.admin.AdminViewModel
import com.example.lolshop.viewmodel.admin.AdminViewModelFactory

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier) {
    val context = LocalContext.current

    // Use ViewModelProvider to get AdminViewModel instance
    val adminViewModel: AdminViewModel = viewModel(factory = AdminViewModelFactory(context))

    // Assuming ProductRepository is a simple class or singleton
    val productRepository = ProductRepository(context) // Replace with actual initialization if necessary

    // Using NavHost to define different destinations
    NavHost(navController = navController, startDestination = Screen.Home.route) {
//        composable(Screen.Home.route) {
//            MainScreen()  // Assuming MainScreen doesn't require any arguments
//        }

        composable(Screen.Admin.route) {
            AdminScreen(
                adminViewModel = adminViewModel,
                productRepository = productRepository // Passing productRepository to AdminScreen
            )
        }

        composable("edit_product/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            if (productId != null) {
                EditProductScreen(
                    productId = productId,
                    adminViewModel = adminViewModel,
                    navController = navController
                )
            }
        }
    }
}
