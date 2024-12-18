package com.example.lolshop.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {
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
