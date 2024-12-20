package com.example.lolshop.view

//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.navigation.NavController
//import androidx.navigation.compose.currentBackStackEntryAsState
//import com.example.lolshop.model.Screen
//
//@Composable
//fun BottomNavigationBar(navController: NavController, isAdmin: Boolean) {
//    val screens = if (isAdmin) {
//        listOf(Screen.Home, Screen.Products, Screen.Admin, Screen.Cart, Screen.Profile)
//    } else {
//        listOf(Screen.Home, Screen.Products, Screen.Cart, Screen.Profile)
//    }
//
//    NavigationBar(containerColor = Color.Black, contentColor = Color.White) {
//        val currentRoute = navController.currentBackStackEntryAsState()?.value?.destination?.route
//
//        screens.forEach { screen ->
//            NavigationBarItem(
//                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
//                label = { Text(screen.title) },
//                selected = currentRoute == screen.route,
//                onClick = { navController.navigate(screen.route) },
//                alwaysShowLabel = true
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//fun BottomNavigationBarPreview() {
//    // Preview without NavController
//}
