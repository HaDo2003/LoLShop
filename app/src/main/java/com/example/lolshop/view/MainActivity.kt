package com.example.lolshop.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lolshop.view.admin.AdminActivity
import com.example.lolshop.view.authentication.LoginActivity
import com.example.lolshop.view.homepage.MainScreen

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Navigate to  LoginActivity
        val intent = Intent(this, MainScreen::class.java)
        startActivity(intent)
        finish()
    }

}

