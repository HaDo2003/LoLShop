package com.example.lolshop.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.lolshop.view.admin.AppNavGraph
import com.example.lolshop.viewmodel.UserRoleViewModel

@Composable
fun MainScreen(userRoleViewModel: UserRoleViewModel) {
    val navController = rememberNavController()
    val isAdmin = userRoleViewModel.isAdmin.collectAsState().value

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController, isAdmin = isAdmin) }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
