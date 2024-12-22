package com.example.lolshop.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lolshop.view.admin.AdminActivity
import com.example.lolshop.view.admin.BannerActivity
import com.example.lolshop.view.authentication.ChangePassword
import com.example.lolshop.view.authentication.LoginActivity
import com.example.lolshop.view.authentication.EmailVerification
import com.example.lolshop.view.authentication.SignUpActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Navigate to  LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
