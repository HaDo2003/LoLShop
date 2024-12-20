package com.example.lolshop.view.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import com.example.lolshop.view.BaseActivity

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SignUpScreen(
                navigateToLogin = {
                    Log.d("LoginScreen", "Navigating to LoginActivity.")
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            )
        }
    }
}
