package com.example.lolshop.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Products : Screen("products", "Products", Icons.Default.List)
    object Admin : Screen("admin", "Admin", Icons.Default.Settings)
    object Cart : Screen("cart", "Cart", Icons.Default.ShoppingCart)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object EditProduct : Screen("edit_product/{productId}", "Edit", Icons.Default.Edit)
}
