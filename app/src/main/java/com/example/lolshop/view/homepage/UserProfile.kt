package com.example.lolshop.view.homepage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lolshop.view.BaseActivity
import com.example.lolshop.view.admin.AdminActivity
import com.example.lolshop.view.theme.LoLShopTheme
import com.example.lolshop.viewmodel.homepage.UserViewModel
import com.example.lolshop.viewmodel.homepage.UserViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.properties.Delegates

class UserProfile : BaseActivity() {
    private lateinit var uid: String
    private var isAdmin by Delegates.notNull<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uid = intent.getStringExtra("uid") ?: ""
        isAdmin = intent.getBooleanExtra("isAdmin", false)
        Log.d("uid", uid)
        Log.d("isAdmin", isAdmin.toString())
        setContent{
            val userViewModel: UserViewModel = viewModel(
                factory = UserViewModelFactory(
                    FirebaseAuth.getInstance(),
                    FirebaseFirestore.getInstance(),
                    applicationContext
                )
            )
            LoLShopTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    color = Color.White
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "user_profile"){
                        composable("user_profile") {
                            val context = LocalContext.current
                            UserProfileScreen(
                                userViewModel,
                                uid,
                                isAdmin,
                                navController = navController,
                                onCartClick = {
                                    val intent = Intent(context, CartActivity::class.java).apply {
                                        putExtra("uid", uid)
                                        putExtra("isAdmin", isAdmin)
                                    }
                                    startActivity(intent)
                                },
                                onProfileClick = {
                                    val intent = Intent(context, UserProfile::class.java).apply {
                                        putExtra("uid", uid)
                                        putExtra("isAdmin", isAdmin)
                                    }
                                    startActivity(intent)
                                },
                                onAdminClick = {
                                    val intent = Intent(context, AdminActivity::class.java).apply {
                                        putExtra("uid", uid)
                                        putExtra("isAdmin", isAdmin)
                                    }
                                    startActivity(intent)
                                },
                                onHomeClick = {
                                    val intent = Intent(context, MainScreen::class.java).apply {
                                        putExtra("uid", uid)
                                        putExtra("isAdmin", isAdmin)
                                    }
                                    startActivity(intent)
                                },
                                onOrderClick = {
                                    val intent = Intent(context, OrderActivity::class.java).apply {
                                        putExtra("uid", uid)
                                        putExtra("isAdmin", isAdmin)
                                    }
                                    startActivity(intent)
                                }
                            )
                        }
                        composable("edit_profile") {
                            UserEditProfileScreen(
                                userViewModel,
                                uid,
                                navController = navController
                            )
                        }
                        composable("change_password") {
                            ChangePasswordScreen(
                                userViewModel,
                                uid,
                                navController = navController
                            )
                        }
                    }

                }
            }
        }
    }
}


