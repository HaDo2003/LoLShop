package com.example.lolshop.view

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lolshop.model.Screen
import com.example.lolshop.viewmodel.AdminViewModel

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier) {
    val context = LocalContext.current

    val adminViewModel = AdminViewModel(context = context)

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.Products.route) { ProductsScreen() }
        composable(Screen.Admin.route) { AdminScreen() }
        composable(Screen.Cart.route) { CartScreen() }
        composable(Screen.Profile.route) { ProfileScreen() }
        composable("edit_product/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            if (productId != null) {
                EditProductScreen(
                    productId = productId,
                    adminViewModel = adminViewModel,
                    navController = navController
                )
            } else {

            }
        }
    }
}

// Dummy screens for illustration
@Composable
fun HomeScreen() { /* UI for Home */ }

@Composable
fun ProductsScreen() { /* UI for Products */ }

@Composable
fun AdminScreen() { /* UI for Admin */ }

@Composable
fun CartScreen() { /* UI for Cart */ }

@Composable
fun ProfileScreen() { /* UI for Profile */ }
