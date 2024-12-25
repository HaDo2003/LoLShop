package com.example.lolshop.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import com.example.lolshop.view.admin.AdminActivity
import com.example.lolshop.view.authentication.LoginActivity
import com.example.lolshop.view.authentication.SignUpActivity
import com.example.lolshop.view.homepage.MainScreen
import com.example.lolshop.view.network.NetworkErrorScreen
import com.example.lolshop.view.theme.LoLShopTheme
import com.example.lolshop.viewmodel.network.NetworkViewModel
import com.example.lolshop.viewmodel.network.NetworkViewModelFactory

enum class ScreenType {
    HOME, ADMIN, LOGIN, SIGNUP
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val networkViewModel: NetworkViewModel by viewModels {
            NetworkViewModelFactory(applicationContext)
        }

        setContent {
            LoLShopTheme {
                val isConnected by networkViewModel.isConnected.collectAsState(initial = false)
                var currentScreen by remember { mutableStateOf(ScreenType.LOGIN) }
                var showNetworkError by remember { mutableStateOf(!isConnected) }

                LaunchedEffect(isConnected) {
                    showNetworkError = !isConnected
                }

                if (showNetworkError) {
                    NetworkErrorScreen()
                } else {
                    when (currentScreen) {
                        ScreenType.HOME -> startActivity(Intent(this@MainActivity, MainScreen::class.java))
                        ScreenType.ADMIN -> startActivity(Intent(this@MainActivity, AdminActivity::class.java))
                        ScreenType.LOGIN -> startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        ScreenType.SIGNUP -> startActivity(Intent(this@MainActivity, SignUpActivity::class.java))
                    }
                }
            }
        }
    }
}
