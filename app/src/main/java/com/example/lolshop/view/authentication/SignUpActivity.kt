package com.example.lolshop.view.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lolshop.view.BaseActivity
import com.example.lolshop.viewmodel.authentication.SignUpViewModel
import com.example.lolshop.viewmodel.authentication.SignUpViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: SignUpViewModel = viewModel(
                factory = SignUpViewModelFactory(
                    FirebaseAuth.getInstance(),
                    FirebaseFirestore.getInstance(),
                    applicationContext
                )
            )
            SignUpScreen(
                viewModel = viewModel,
                navigateToLogin = {
                    Log.d("LoginScreen", "Navigating to LoginActivity.")
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                navigateToOtp = { name, email, password, phoneNumber, address ->
                    val intent = Intent(this, EmailVerification::class.java)
                    intent.putExtra("name", name)
                    intent.putExtra("email", email)
                    intent.putExtra("password", password)
                    intent.putExtra("phoneNumber", phoneNumber)
                    intent.putExtra("address", address)
                    startActivity(intent)
                }
            )
        }
    }
}
