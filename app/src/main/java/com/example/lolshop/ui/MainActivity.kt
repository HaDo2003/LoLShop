package com.example.lolshop.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Navigate to  LoginActivity
        val intent = Intent(this, AdminActivity::class.java)
        startActivity(intent)
        finish()
    }
}
